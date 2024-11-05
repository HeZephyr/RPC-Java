package rpc.v3.balancing;

import rpc.v3.balancing.common.service.UserService;
import rpc.v3.balancing.common.service.impl.UserServiceImpl;
import rpc.v3.balancing.server.RpcServer;
import rpc.v3.balancing.server.impl.NettyRpcServer;
import rpc.v3.balancing.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
