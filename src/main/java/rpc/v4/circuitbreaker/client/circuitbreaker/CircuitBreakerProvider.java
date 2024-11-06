package rpc.v4.circuitbreaker.client.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CircuitBreakerProvider {
    private final Map<String, CircuitBreaker> circuitBreakerMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(CircuitBreakerProvider.class);

    public synchronized  CircuitBreaker getCircuitBreaker(String serviceName) {
        CircuitBreaker circuitBreaker;
        if (!circuitBreakerMap.containsKey(serviceName)) {
            circuitBreaker = new CircuitBreaker(1, 0.5, 10000);
            circuitBreakerMap.put(serviceName, circuitBreaker);
            logger.info("CircuitBreaker initialized for service: {}", serviceName);
            return circuitBreaker;
        } else {
            return circuitBreakerMap.get(serviceName);
        }
    }
}
