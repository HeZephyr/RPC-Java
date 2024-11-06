package rpc.v1.basic;

import rpc.v1.basic.common.service.UserService;
import rpc.v1.basic.common.service.impl.UserServiceImpl;
import rpc.v1.basic.server.RpcServer;
import rpc.v1.basic.server.impl.ThreadPoolRpcServer;
import rpc.v1.basic.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        // Initialize the service provider and add services
        ServiceProvider serviceProvider = new ServiceProvider();
        UserService userService = new UserServiceImpl();
        serviceProvider.provideServiceInterface(userService);

        // Start the RPC server
        RpcServer rpcServer = new ThreadPoolRpcServer(serviceProvider);
        rpcServer.start(9999);

        // Note: This server will keep running until manually stopped or interrupted
    }
}
