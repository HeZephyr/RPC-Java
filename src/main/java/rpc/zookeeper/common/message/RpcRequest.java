package rpc.zookeeper.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * RpcRequest represents a remote procedure call request message.
 * It contains details about the method to be invoked on the remote service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // The name of the interface to be invoked, which is used to find the corresponding service
    private String interfaceName;
    // The name of the method to be invoked
    private String methodName;
    // The parameters of the method to be invoked
    private Object[] parameters;
    // The types of the parameters of the method to be invoked
    private Class<?>[] paramTypes;
}