package rpc.v4.limiter.server.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress serverAddress, boolean canRetry);
}
