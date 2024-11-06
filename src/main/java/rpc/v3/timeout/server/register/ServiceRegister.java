package rpc.v3.timeout.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serverAddress, boolean canRetry);
}
