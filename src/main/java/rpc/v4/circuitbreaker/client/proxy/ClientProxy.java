package rpc.v4.circuitbreaker.client.proxy;

import rpc.v4.circuitbreaker.client.RpcClient;
import rpc.v4.circuitbreaker.client.circuitbreaker.CircuitBreaker;
import rpc.v4.circuitbreaker.client.circuitbreaker.CircuitBreakerProvider;
import rpc.v4.circuitbreaker.client.discovery.ServiceDiscovery;
import rpc.v4.circuitbreaker.client.discovery.impl.ZKServiceDiscovery;
import rpc.v4.circuitbreaker.client.impl.NettyRpcClient;
import rpc.v4.circuitbreaker.client.retry.GuavaRetry;
import rpc.v4.circuitbreaker.common.message.RpcRequest;
import rpc.v4.circuitbreaker.common.message.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * ClientProxy is a client-side proxy class that uses JDK dynamic proxy.
 * It implements InvocationHandler to intercept method calls and send RPC requests.
 */
public class ClientProxy implements InvocationHandler {

    // Client used to send RPC requests
    private final RpcClient rpcClient;
    // Service discovery for identifying available services
    private final ServiceDiscovery serviceDiscovery;
    // Provider of circuit breakers for managing different services
    private final CircuitBreakerProvider circuitBreakerProvider;

    // Constructor that initializes RPC client, service discovery, and circuit breaker provider
    public ClientProxy() {
        this.rpcClient = new NettyRpcClient();
        this.serviceDiscovery = new ZKServiceDiscovery();
        this.circuitBreakerProvider = new CircuitBreakerProvider();
    }

    /**
     * Intercepts method calls on the proxy instance and manages RPC requests.
     *
     * @param proxy  the proxy instance on which the method was invoked
     * @param method the method that was invoked
     * @param args   the arguments for the method
     * @return the result of the method call or null if circuit breaker blocks the request
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // Build an RPC request based on the method details and arguments
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName()) // Target interface name
                .methodName(method.getName())                        // Target method name
                .parameters(args)                                    // Method arguments
                .paramTypes(method.getParameterTypes())              // Method parameter types
                .build();

        // Obtain a circuit breaker for the method
        CircuitBreaker circuitBreaker = circuitBreakerProvider.getCircuitBreaker(method.getName());

        // Check if the circuit breaker allows the request to proceed
        if (!circuitBreaker.allowRequest()) {
            return null;  // If the circuit is open, request is blocked
        }

        RpcResponse rpcResponse;

        // Check if retry mechanism is required for this service and use retry if necessary
        if (serviceDiscovery.checkRetry(rpcRequest.getInterfaceName())) {
            rpcResponse = new GuavaRetry().sendRequestWithRetry(rpcRequest, rpcClient);
        } else {
            // Send RPC request directly if no retry is required
            rpcResponse = rpcClient.sendRequest(rpcRequest);
        }

        // Update circuit breaker based on the response status
        if (rpcResponse.getCode() == 200) {
            circuitBreaker.recordSuccess();  // Log a successful response
        }
        if (rpcResponse.getCode() == 500) {
            circuitBreaker.recordFailure();  // Log a failure response
        }

        // Return the data from the RPC response
        return rpcResponse.getData();
    }

    /**
     * Creates a proxy instance for the specified interface. All method calls
     * are intercepted and handled by the invoke method in this class.
     *
     * @param clazz the interface class to create a proxy for
     * @param <T>   the type of the interface
     * @return a proxy instance of the interface
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        // Use JDK dynamic proxy to create a proxy instance for the interface
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}