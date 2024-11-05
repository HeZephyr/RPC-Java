package rpc.custom.client;

import rpc.custom.common.message.RpcRequest;
import rpc.custom.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
