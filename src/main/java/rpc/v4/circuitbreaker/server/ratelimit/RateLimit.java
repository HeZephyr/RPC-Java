package rpc.v4.circuitbreaker.server.ratelimit;

public interface RateLimit {
    boolean getToken();
}
