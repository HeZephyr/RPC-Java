package rpc.v4.circuitbreaker.server.ratelimit.provider;

import rpc.v4.circuitbreaker.server.ratelimit.RateLimit;
import rpc.v4.circuitbreaker.server.ratelimit.impl.TokenBucketRateLimit;

import java.util.HashMap;
import java.util.Map;

public class RateLimitProvider {
    private final Map<String, RateLimit> rateLimitMap = new HashMap<>();

    public RateLimit getRateLimit(String interfaceName) {
        if (!rateLimitMap.containsKey(interfaceName)) {
            RateLimit rateLimit = new TokenBucketRateLimit(100, 10);
            rateLimitMap.put(interfaceName, rateLimit);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }
}
