package rpc.v4.limiter.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    // Discover the service address for a given service name
    InetSocketAddress discoveryService(String serviceName);
    // Check if a service should be retried
    boolean checkRetry(String serviceName);
}
