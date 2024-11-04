package rpc.netty.client.proxy;

import rpc.netty.common.message.RpcRequest;
import rpc.netty.client.RpcClient;
import rpc.netty.client.impl.NettyRpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ClientProxy is a client-side proxy class based on JDK dynamic proxy.
 * It implements InvocationHandler to intercept method calls and send RPC requests.
 */
public class ClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;
    public ClientProxy(String host, int port) {
        this.rpcClient = new NettyRpcClient(host, port);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();
        return rpcClient.sendRequest(rpcRequest).getData();
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
