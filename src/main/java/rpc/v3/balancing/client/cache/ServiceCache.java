package rpc.v3.balancing.client.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCache {
    // key: serviceName, value: the service address list
    private final Map<String, List<String>> serviceCache = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(ServiceCache.class);

    // add service address to cache
    public void addServiceAddress(String serviceName, String address) {
        logger.info("Add service address [{}] for service: [{}]", address, serviceName);
        if (serviceCache.containsKey(serviceName)) {
            List<String> addresses = serviceCache.get(serviceName);
            if (!addresses.contains(address)) {
                addresses.add(address);
            }
        } else {
            List<String> addresses = new ArrayList<>();
            addresses.add(address);
            serviceCache.put(serviceName, addresses);
        }
    }

    // update service address in cache
    public void updateServiceAddress(String serviceName, String oldAddress, String newAddress) {
        if (serviceCache.containsKey(serviceName)) {
            List<String> addressList = serviceCache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
            logger.info("Update service address for service: [{}]", serviceName);
        } else {
            logger.error("Service: [{}] not found in service cache", serviceName);
        }
    }

    // get service address list by service name
    public List<String> getServiceAddress(String serviceName) {
        logger.info("Get service address for service: [{}]", serviceName);
        return serviceCache.get(serviceName);
    }

    // remove service address from cache
    public void removeServiceAddress(String serviceName, String address) {
        List<String> addressList = serviceCache.get(serviceName);
        addressList.remove(address);
        logger.info("Remove service address [{}] for service: [{}]", address, serviceName);
    }
}
