package rpc.v2.cache.client;

import rpc.v2.cache.common.message.RpcRequest;
import rpc.v2.cache.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
