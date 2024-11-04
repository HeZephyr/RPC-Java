package rpc.netty.client;

import rpc.netty.common.message.RpcRequest;
import rpc.netty.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
