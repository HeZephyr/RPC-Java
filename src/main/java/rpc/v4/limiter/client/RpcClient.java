package rpc.v4.limiter.client;

import rpc.v4.limiter.common.message.RpcRequest;
import rpc.v4.limiter.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
