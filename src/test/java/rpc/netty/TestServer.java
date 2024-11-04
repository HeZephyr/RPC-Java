package rpc.netty;

import rpc.netty.common.service.UserService;
import rpc.netty.common.service.impl.UserServiceImpl;
import rpc.netty.server.RpcServer;
import rpc.netty.server.impl.NettyRpcServer;
import rpc.netty.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
