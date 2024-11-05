package rpc.v3.balancing.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * RpcResponse represents the response message for an RPC request.
 * It includes the status code, message, and response data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // The status code of the response (e.g., 200 for success, 500 for error).
    private int code;
    // The error message if the request failed, or null if successful.
    private String message;
    // The data returned by the server in response to the RPC request.
    private Object data;
    private Class<?> dataType;
    /**
     * Creates a successful RpcResponse with the specified data.
     *
     * @param data the data to include in the successful response
     * @return a successful RpcResponse containing the given data
     */
    public static RpcResponse success(Object data) {
        return RpcResponse.builder()
                .code(200)
                .dataType(data.getClass())
                .data(data)
                .build();
    }
    /**
     * Creates a failed RpcResponse with a default error message.
     *
     * @return a failed RpcResponse with a status code of 500 and a default error message
     */
    public static RpcResponse fail(int code, String message) {
        return RpcResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}
