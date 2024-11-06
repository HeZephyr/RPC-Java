package rpc.v2.custom.client;

import rpc.v2.custom.common.message.RpcRequest;
import rpc.v2.custom.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
