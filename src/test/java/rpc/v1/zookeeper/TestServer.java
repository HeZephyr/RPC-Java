package rpc.v1.zookeeper;

import rpc.v1.zookeeper.common.service.UserService;
import rpc.v1.zookeeper.common.service.impl.UserServiceImpl;
import rpc.v1.zookeeper.server.RpcServer;
import rpc.v1.zookeeper.server.impl.NettyRpcServer;
import rpc.v1.zookeeper.server.provider.ServiceProvider;

public class TestServer {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 9999);
        serviceProvider.provideServiceInterface(userService);

        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);
    }
}
