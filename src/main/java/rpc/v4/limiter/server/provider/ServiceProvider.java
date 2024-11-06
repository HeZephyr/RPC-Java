package rpc.v4.limiter.server.provider;

import lombok.Getter;
import rpc.v4.limiter.server.ratelimit.provider.RateLimitProvider;
import rpc.v4.limiter.server.register.ServiceRegister;
import rpc.v4.limiter.server.register.impl.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceProvider is responsible for storing and providing service implementations.
 * It registers service instances based on their implemented interfaces, allowing clients
 * to retrieve services by interface name.
 */
public class ServiceProvider {

    // Thread-safe map to store service interfaces and their implementations
    private final Map<String, Object> interfaceProvider;

    // Host and port information for the service, used when registering in Zookeeper
    private final String host;
    private final int port;

    // Service register component responsible for registering service instances in Zookeeper
    private final ServiceRegister serviceRegister;
    // rate limiter
    @Getter
    private final RateLimitProvider rateLimitProvider;

    /**
     * Constructs a ServiceProvider instance with specified host and port.
     * Initializes the service register and the storage for service interfaces.
     *
     * @param host the host where the service is hosted
     * @param port the port on which the service is available
     */
    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.interfaceProvider = new ConcurrentHashMap<>();  // Use ConcurrentHashMap for thread safety
        this.serviceRegister = new ZKServiceRegister();  // Initializes the service register using Zookeeper
        this.rateLimitProvider = new RateLimitProvider();
    }

    /**
     * Registers a service instance for each interface it implements.
     * The service instance is stored in a local map, and its address is registered in Zookeeper.
     *
     * @param service the service instance to be registered
     * @throws IllegalArgumentException if the service does not implement any interface
     */
    public void provideServiceInterface(Object service, boolean canRetry) {
        // Get the list of interfaces it implements
        Class<?>[] interfaces = service.getClass().getInterfaces();

        // Register each implemented interface with the service instance
        for (Class<?> clazz : interfaces) {
            // Store the service in the local map with the interface name as the key
            interfaceProvider.put(clazz.getName(), service);
            // Register the service with its address in Zookeeper
            serviceRegister.register(clazz.getName(), new InetSocketAddress(host, port), canRetry);
        }
    }

    /**
     * Retrieves a service instance by the interface name.
     * Looks up the interface name in the local map to find the registered service instance.
     *
     * @param interfaceName the name of the service interface
     * @return the service instance implementing the specified interface, or null if not found
     */
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}