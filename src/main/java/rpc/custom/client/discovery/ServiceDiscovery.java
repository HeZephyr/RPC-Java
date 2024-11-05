package rpc.custom.client.discovery;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress discoveryService(String serviceName);
}
