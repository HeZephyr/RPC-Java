package rpc.v3.balancing.client;

import rpc.v3.balancing.common.message.RpcRequest;
import rpc.v3.balancing.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
