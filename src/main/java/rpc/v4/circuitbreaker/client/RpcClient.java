package rpc.v4.circuitbreaker.client;

import rpc.v4.circuitbreaker.common.message.RpcRequest;
import rpc.v4.circuitbreaker.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
