package rpc.basic;

import rpc.basic.common.service.UserService;
import rpc.basic.common.service.impl.UserServiceImpl;
import rpc.basic.server.RpcServer;
import rpc.basic.server.impl.SimpleRpcServer;
import rpc.basic.server.impl.ThreadPoolRpcServer;
import rpc.basic.server.provider.ServiceProvider;

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
