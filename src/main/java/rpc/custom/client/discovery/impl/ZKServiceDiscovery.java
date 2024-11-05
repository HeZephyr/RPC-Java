package rpc.custom.client.discovery.impl;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.custom.client.discovery.ServiceDiscovery;

import java.net.InetSocketAddress;
import java.util.List;

public class ZKServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZKServiceDiscovery.class);

    private final CuratorFramework client;
    private static final String ZK_REGISTER_ROOT_PATH = "my-rpc";
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
    }

    @Override
    public InetSocketAddress discoveryService(String serviceName) {
        try {
            List<String> serviceAddresses = client.getChildren().forPath("/" + serviceName);
            // Load balancing: simple random strategy
            int size = serviceAddresses.size();
            int index = (int) (Math.random() * size);
            String serviceAddress = serviceAddresses.get(index);
            logger.info("Discovered service instance: {} for service: {}", serviceAddress, serviceName);
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
}
