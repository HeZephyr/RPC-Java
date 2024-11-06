package rpc.v3.timeout.client;

import rpc.v3.timeout.common.message.RpcRequest;
import rpc.v3.timeout.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
