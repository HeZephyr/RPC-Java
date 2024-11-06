package rpc.v3.timeout.client.retry;

import com.github.rholder.retry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v3.timeout.client.RpcClient;
import rpc.v3.timeout.common.message.RpcRequest;
import rpc.v3.timeout.common.message.RpcResponse;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * GuavaRetry is responsible for sending an RPC request with retry logic.
 * It utilizes the Guava Retryer library to handle retry attempts and exceptions
 * when sending a request to the RPC server.
 */
public class GuavaRetry {
    private RpcClient rpcClient;
    private static final Logger logger = LoggerFactory.getLogger(GuavaRetry.class);

    /**
     * Sends an RPC request with retry logic. If the response indicates a failure or an exception occurs,
     * the request is retried up to a specified number of attempts.
     *
     * @param rpcRequest The RPC request to be sent.
     * @param client The RpcClient used to send the request.
     * @return RpcResponse The response received from the server, or a failure response if retries are exhausted.
     */
    public RpcResponse sendRequestWithRetry(RpcRequest rpcRequest, RpcClient client) {
        this.rpcClient = client;

        // Configure retryer with retry conditions, wait strategy, and stop strategy
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException() // Retry if any exception occurs during the request
                .retryIfResult(response -> Objects.equals(response.getCode(), 500)) // Retry if response code is 500
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS)) // Wait 2 seconds between retries
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // Stop after 3 attempts
                .withRetryListener(new RetryListener() { // Log each retry attempt
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        logger.info("Retrying... attempt: {}", attempt.getAttemptNumber());
                    }
                })
                .build();

        try {
            // Attempt to send the RPC request with retry logic
            return retryer.call(() -> rpcClient.sendRequest(rpcRequest));
        } catch (ExecutionException e) {
            // Log and return a failure response if execution fails
            logger.error("ExecutionException: {}", e.getMessage());
            return RpcResponse.fail(500, "ExecutionException");
        } catch (RetryException e) {
            // Log and return a failure response if retry limit is reached
            logger.error("RetryException: {}", e.getMessage());
            return RpcResponse.fail(500, "RetryException");
        }
    }
}