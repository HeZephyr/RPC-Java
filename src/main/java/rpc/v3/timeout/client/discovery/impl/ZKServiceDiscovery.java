package rpc.v3.timeout.client.discovery.impl;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v3.timeout.client.cache.ServiceCache;
import rpc.v3.timeout.client.discovery.ServiceDiscovery;
import rpc.v3.timeout.client.discovery.balance.impl.ConsistencyHashBalance;
import rpc.v3.timeout.client.discovery.watcher.ZKWatcher;

import java.net.InetSocketAddress;
import java.util.List;


public class ZKServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private final CuratorFramework client;
    private final ServiceCache serviceCache;
    private static final String ZK_REGISTER_ROOT_PATH = "my-rpc";
    private static final String RETRY = "retry";
    // ZooKeeper connection string and session timeout settings
    private static final String ZK_CONNECT_STRING = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT_MS = 5000;
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;

    public ZKServiceDiscovery() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        this.client = CuratorFrameworkFactory.builder()
                .connectString(ZK_CONNECT_STRING)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .retryPolicy(retryPolicy)
                .namespace(ZK_REGISTER_ROOT_PATH)
                .build();
        this.client.start();
        logger.info("Connected to ZooKeeper at {}", ZK_CONNECT_STRING);

        // initialize the service cache
        this.serviceCache = new ServiceCache();
        // add the watcher to the service cache
        new ZKWatcher(client, serviceCache).watchNode(ZK_REGISTER_ROOT_PATH);
    }

    @Override
    public InetSocketAddress discoveryService(String serviceName) {
        try {
            // find the local cache of the service address list
            List<String> serviceList = serviceCache.getServiceAddress(serviceName);
            if (serviceList == null) {
                serviceList = client.getChildren().forPath("/" + serviceName);
            }
            // load balance to select a service instance
            String serviceAddress = new ConsistencyHashBalance().balance(serviceList);
            logger.info("Successfully discovered service instance: {}", serviceAddress);
            return parseServiceAddress(serviceAddress);
        } catch (Exception e) {
            logger.error("Failed to discover service instance for service: {}", serviceName);
            throw new RuntimeException("Failed to discover service instance", e);
        }
    }

    /**
     * Formats the network address as a string in "hostname:port" format.
     * @param serverAddress   The network address of the service instance
     * @return A string representing the hostname and port
     */
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() + ":" + serverAddress.getPort();
    }

    /**
     * Parses a network address string into an InetSocketAddress.
     * @param serviceAddress  The address in "hostname:port" format
     * @return Parsed InetSocketAddress object
     */
    private InetSocketAddress parseServiceAddress(String serviceAddress) {
        String[] addressComponents = serviceAddress.split(":");
        String host = addressComponents[0];
        int port = Integer.parseInt(addressComponents[1]);
        return new InetSocketAddress(host, port);
    }

    @Override
    public boolean checkRetry(String serviceName) {
        boolean canRetry = false;
        try {
            List<String> serviceList = client.getChildren().forPath("/" + RETRY);
            for (String s : serviceList) {
                if (s.equals(serviceName)) {
                    canRetry = true;
                    logger.info("Service {} is retryable", serviceName);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check if service {} is retryable", serviceName);
            throw new RuntimeException("Failed to check if service is retryable", e);
        }
        return canRetry;
    }
}
