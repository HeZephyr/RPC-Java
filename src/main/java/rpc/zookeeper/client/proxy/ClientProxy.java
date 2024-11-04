package rpc.zookeeper.client.proxy;

import rpc.zookeeper.client.RpcClient;
import rpc.zookeeper.client.impl.NettyRpcClient;
import rpc.zookeeper.common.message.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ClientProxy is a client-side proxy class based on JDK dynamic proxy.
 * It implements InvocationHandler to intercept method calls and send RPC requests.
 */
public class ClientProxy implements InvocationHandler {

    private final RpcClient rpcClient;
    public ClientProxy() {
        this.rpcClient = new NettyRpcClient();
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
