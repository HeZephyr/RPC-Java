package rpc.v2.cache;

import rpc.v2.cache.common.service.UserService;
import rpc.v2.cache.common.service.impl.UserServiceImpl;
import rpc.v2.cache.server.RpcServer;
import rpc.v2.cache.server.impl.NettyRpcServer;
import rpc.v2.cache.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
