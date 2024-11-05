package rpc.v2.custom;

import rpc.v2.custom.common.service.UserService;
import rpc.v2.custom.common.service.impl.UserServiceImpl;
import rpc.v2.custom.server.RpcServer;
import rpc.v2.custom.server.impl.NettyRpcServer;
import rpc.v2.custom.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
