package rpc.v4.circuitbreaker.client.circuitbreaker;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    // Tracks the current state of the circuit breaker
    @Getter
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;

    // Counts failures, successes, and requests, respectively
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger requestCount = new AtomicInteger(0);

    // Threshold for the number of failures before the circuit opens
    private final int failureThreshold;
    // Percentage of successful requests required to close the circuit from HALF_OPEN state
    private final double successThreshold;
    // Time in milliseconds after which the circuit will attempt to reclose
    private final long retryTimePeriod;
    // Tracks the last failure time
    private long lastFailureTime = 0;

    private final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);

    public CircuitBreaker(int failureThreshold, double successThreshold, long retryTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.retryTimePeriod = retryTimePeriod;
    }

    /**
     * Determines if a request should be allowed based on the circuit's state
     *
     * @return true if the request is allowed, false if denied
     */
    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();
        logger.info("Before allowRequest, state: {}, failureCount: {}, successCount: {}, requestCount: {}", state, failureCount.get(), successCount.get(), requestCount.get());

        switch (state) {
            case OPEN -> {
                // If the circuit is OPEN and retry period has passed, switch to HALF_OPEN to test connection
                if (currentTime - lastFailureTime > retryTimePeriod) {
                    state = CircuitBreakerState.HALF_OPEN;
                    resetCounts();
                    return true;  // Allow request to test the connection
                }
                // If within retry time, deny the request
                logger.info("Circuit is OPEN, request denied.");
                return false;
            }
            case HALF_OPEN -> {
                // In HALF_OPEN, count the request and allow it to test if the service has recovered
                requestCount.incrementAndGet();
                return true;
            }
            default -> {
                // In CLOSED state, allow all requests
                return true;
            }
        }
    }

    /**
     * Records a successful request and updates the circuit state if in HALF_OPEN
     */
    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount.incrementAndGet();
            // If the success threshold is met, close the circuit
            if (successCount.get() >= successThreshold * requestCount.get()) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
            }
        } else {
            resetCounts();  // Reset counts when in CLOSED state
        }
    }

    /**
     * Records a failed request and updates the circuit state if in HALF_OPEN
     */
    public synchronized void recordFailure() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            failureCount.incrementAndGet();
            lastFailureTime = System.currentTimeMillis();
            // If failure threshold is reached, open the circuit
            if (failureCount.get() >= failureThreshold) {
                state = CircuitBreakerState.OPEN;
            }
        } else {
            resetCounts();  // Reset counts when in CLOSED state
        }
    }

    /**
     * Resets all request, success, and failure counts to zero
     */
    private void resetCounts() {
        failureCount.set(0);
        successCount.set(0);
        requestCount.set(0);
    }
}

// Defines the possible states for the circuit breaker
enum CircuitBreakerState {
    CLOSED,    // Circuit is closed, allowing all requests
    OPEN,      // Circuit is open, denying all requests
    HALF_OPEN  // Circuit is half-open, allowing limited requests to test recovery
}