package rpc.v2.custom.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress discoveryService(String serviceName);
}
