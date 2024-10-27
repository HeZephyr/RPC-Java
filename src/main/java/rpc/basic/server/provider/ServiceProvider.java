package rpc.basic.server.provider;

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

    public ServiceProvider() {
        this.interfaceProvider = new ConcurrentHashMap<>();
    }

    /**
     * Registers a service instance for each interface it implements.
     *
     * @param service the service instance to be registered
     * @throws IllegalArgumentException if the service does not implement any interface
     */
    public void provideServiceInterface(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();

        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Service must implement at least one interface");
        }

        for (Class<?> clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
        }
    }

    /**
     * Retrieves a service instance by the interface name.
     *
     * @param interfaceName the name of the service interface
     * @return the service instance implementing the specified interface, or null if not found
     */
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
