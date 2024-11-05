package rpc.v1.netty;

import rpc.v1.netty.common.service.UserService;
import rpc.v1.netty.common.service.impl.UserServiceImpl;
import rpc.v1.netty.server.RpcServer;
import rpc.v1.netty.server.impl.NettyRpcServer;
import rpc.v1.netty.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
