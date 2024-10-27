package rpc.basic.client.proxy;

import lombok.AllArgsConstructor;
import rpc.basic.client.transport.IOClient;
import rpc.basic.common.message.RpcRequest;
import rpc.basic.common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ClientProxy is a client-side proxy class based on JDK dynamic proxy.
 * It implements InvocationHandler to intercept method calls and send RPC requests.
 */
@AllArgsConstructor
public class ClientProxy implements InvocationHandler {

    private final String host;
    private final int port;

    /**
     * Intercepts proxy method calls, converts them to RPC requests, sends them to the remote server,
     * and returns the result of the remote invocation.
     *
     * @param proxy  the proxy instance
     * @param method the method being called
     * @param args   the arguments passed to the method
     * @return the result of the remote method invocation
     * @throws Throwable if any exception occurs
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Build the RPC request object, containing interface name, method name, parameters, and parameter types
        RpcRequest request = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameters(args)
                .paramTypes(method.getParameterTypes())
                .build();

        // Send the request via IOClient and get the response data
        RpcResponse response = IOClient.sendRequest(host, port, request);

        // Return the result of the remote invocation
        return response.getData();
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
