package rpc.v4.circuitbreaker.server.ratelimit.impl;

import rpc.v4.circuitbreaker.server.ratelimit.RateLimit;

/**
 * TokenBucketRateLimit implements a rate-limiting mechanism using the token bucket algorithm.
 * Tokens are generated at a fixed rate up to a maximum bucket capacity.
 * If there are tokens available, requests are allowed; otherwise, requests are denied until tokens are refilled.
 */
public class TokenBucketRateLimit implements RateLimit {
    // The rate (in milliseconds) at which tokens are generated
    private static int RATE;

    // The maximum capacity of the token bucket
    private static int CAPACITY;

    // Current count of tokens in the bucket
    private int tokens;

    // Timestamp of the last token refill
    private volatile long timestamp = System.currentTimeMillis();

    // Constructor to set up the rate and capacity of the token bucket
    public TokenBucketRateLimit(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        tokens = CAPACITY;  // Initially, the bucket is full
    }

    /**
     * Attempts to retrieve a token from the bucket. If a token is available, it is consumed,
     * and the method returns true. Otherwise, tokens may be refilled based on the elapsed time
     * since the last token generation, and then it reattempts to retrieve a token.
     *
     * @return true if a token is successfully retrieved; false otherwise
     */
    @Override
    public synchronized boolean getToken() {
        // Check if there are tokens available for immediate use
        if (tokens > 0) {
            tokens--;  // Consume a token
            return true;
        }

        // Calculate the current time and the time since the last token refill
        long currentTime = System.currentTimeMillis();

        // If enough time has passed since the last refill, refill the tokens
        if (currentTime - timestamp >= RATE) {
            // Calculate the number of tokens that should be refilled based on elapsed time
            if ((currentTime - timestamp) / RATE >= 2) {
                tokens += (int) (currentTime - timestamp) / RATE - 1;
            }

            // Ensure that the token count does not exceed the bucket capacity
            if (tokens > CAPACITY) {
                tokens = CAPACITY;
            }

            // Update the timestamp to the current time
            timestamp = currentTime;

            // Allow the current request as the bucket has been refilled
            return true;
        }

        // Return false if there are no tokens available and refilling conditions are not met
        return false;
    }
}