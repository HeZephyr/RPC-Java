package rpc.v2.cache.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress discoveryService(String serviceName);
}
