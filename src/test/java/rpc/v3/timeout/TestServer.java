package rpc.v3.timeout;

import rpc.v3.timeout.common.service.UserService;
import rpc.v3.timeout.common.service.impl.UserServiceImpl;
import rpc.v3.timeout.server.RpcServer;
import rpc.v3.timeout.server.impl.NettyRpcServer;
import rpc.v3.timeout.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService, true);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
