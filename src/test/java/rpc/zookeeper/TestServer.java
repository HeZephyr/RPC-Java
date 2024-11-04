package rpc.zookeeper;

import rpc.zookeeper.common.service.UserService;
import rpc.zookeeper.common.service.impl.UserServiceImpl;
import rpc.zookeeper.server.RpcServer;
import rpc.zookeeper.server.impl.NettyRpcServer;
import rpc.zookeeper.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
