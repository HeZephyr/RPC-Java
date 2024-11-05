package rpc.custom;

import rpc.custom.common.service.UserService;
import rpc.custom.common.service.impl.UserServiceImpl;
import rpc.custom.server.RpcServer;
import rpc.custom.server.impl.NettyRpcServer;
import rpc.custom.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
