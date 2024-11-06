package rpc.v4.limiter.server.ratelimit;

public interface RateLimit {
    boolean getToken();
}
