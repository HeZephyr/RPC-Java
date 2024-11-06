package rpc.v4.circuitbreaker;

import rpc.v4.circuitbreaker.common.service.UserService;
import rpc.v4.circuitbreaker.common.service.impl.UserServiceImpl;
import rpc.v4.circuitbreaker.server.RpcServer;
import rpc.v4.circuitbreaker.server.impl.NettyRpcServer;
import rpc.v4.circuitbreaker.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService, true);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
