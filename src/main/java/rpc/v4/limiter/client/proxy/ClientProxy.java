package rpc.v4.limiter.client.proxy;

import rpc.v4.limiter.client.RpcClient;
import rpc.v4.limiter.client.discovery.ServiceDiscovery;
import rpc.v4.limiter.client.discovery.impl.ZKServiceDiscovery;
import rpc.v4.limiter.client.impl.NettyRpcClient;
import rpc.v4.limiter.client.retry.GuavaRetry;
import rpc.v4.limiter.common.message.RpcRequest;
import rpc.v4.limiter.common.message.RpcResponse;

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
