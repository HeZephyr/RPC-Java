package rpc.v3.balancing.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress discoveryService(String serviceName);
}
