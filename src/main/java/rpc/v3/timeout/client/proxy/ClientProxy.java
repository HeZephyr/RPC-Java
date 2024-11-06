package rpc.v3.timeout.client.proxy;

import rpc.v3.timeout.client.RpcClient;
import rpc.v3.timeout.client.discovery.ServiceDiscovery;
import rpc.v3.timeout.client.discovery.impl.ZKServiceDiscovery;
import rpc.v3.timeout.client.impl.NettyRpcClient;
import rpc.v3.timeout.client.retry.GuavaRetry;
import rpc.v3.timeout.common.message.RpcRequest;
import rpc.v3.timeout.common.message.RpcResponse;
import rpc.v3.timeout.server.register.ServiceRegister;
import rpc.v3.timeout.server.register.impl.ZKServiceRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ClientProxy is a client-side proxy class based on JDK dynamic proxy.
 * It implements InvocationHandler to intercept method calls and send RPC requests.
 */
public class ClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;
    private final ServiceDiscovery serviceDiscovery;
    public ClientProxy() {
        this.rpcClient = new NettyRpcClient();
        this.serviceDiscovery = new ZKServiceDiscovery();
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        RpcResponse rpcResponse;
        if (serviceDiscovery.checkRetry(rpcRequest.getInterfaceName())) {
            rpcResponse = new GuavaRetry().sendRequestWithRetry(rpcRequest, rpcClient);
        } else {
            rpcResponse = rpcClient.sendRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }
    /**
     * Creates a proxy instance for the specified interface, allowing method calls
     * to be intercepted and processed by the invoke method.
     *
     * @param clazz the interface class
     * @param <T>   the type of the interface
     * @return a proxy instance of the interface
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // Use JDK dynamic proxy to create a proxy instance for the interface
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
