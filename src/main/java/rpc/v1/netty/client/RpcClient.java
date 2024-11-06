package rpc.v1.netty.client;

import rpc.v1.netty.common.message.RpcRequest;
import rpc.v1.netty.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
