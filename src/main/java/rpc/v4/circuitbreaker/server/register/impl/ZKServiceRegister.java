package rpc.v4.circuitbreaker.server.register.impl;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.circuitbreaker.server.register.ServiceRegister;

import java.net.InetSocketAddress;

public class ZKServiceRegister implements ServiceRegister {
    // Logger for logging information and errors
    private static final Logger logger = LoggerFactory.getLogger(ZKServiceRegister.class);

    // Curator framework client to interact with ZooKeeper
    private final CuratorFramework client;

    // Root path in ZooKeeper for all registered services
    private static final String ZK_REGISTER_ROOT_PATH = "my-rpc";
    private static final String RETRY = "retry";

    // ZooKeeper connection string and session timeout settings
    private static final String ZK_CONNECT_STRING = "127.0.0.1:2181";
    private static final int SESSION_TIMEOUT_MS = 5000;
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;

    // Constructor to initialize and start the ZooKeeper client
    public ZKServiceRegister() {
        // Retry policy: retry with exponential backoff, 3 times with an interval starting at 1 second
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);

        // Build and start the Curator framework client with namespace set to root path
        this.client = CuratorFrameworkFactory.builder()
                .connectString(ZK_CONNECT_STRING)
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .retryPolicy(retryPolicy)
                .namespace(ZK_REGISTER_ROOT_PATH)
                .build();

        // Start the ZooKeeper client
        this.client.start();
        logger.info("Connected to ZooKeeper at {}", ZK_CONNECT_STRING);
    }

    /**
     * Registers the service instance with ZooKeeper.
     * @param serviceName     The name of the service to register
     * @param serverAddress   The network address (IP and port) of the service instance
     */
    @Override
    public void register(String serviceName, InetSocketAddress serverAddress, boolean canRetry) {
        try {
            // Check if the service node exists; if not, create a persistent node for the service
            // When the service provider goes offline, the service name is not deleted, only the address is deleted.
            if (client.checkExists().forPath("/" + serviceName) == null) {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath("/" + serviceName);
                logger.info("Created persistent node for service: {}", serviceName);
            }

            // Create an ephemeral node for the specific service instance (IP and port)
            // Delete the node when the server goes offline
            String serviceInstancePath = "/" + serviceName + "/" + getServiceAddress(serverAddress);
            client.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(serviceInstancePath);
            logger.info("Registered service instance at path: {}", serviceInstancePath);

            if (canRetry) {
                serviceInstancePath = "/" + RETRY + "/" + serviceName;
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(serviceInstancePath);
            }
        } catch (Exception e) {
            logger.error("Failed to register service {} with address {}", serviceName, serverAddress, e);
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