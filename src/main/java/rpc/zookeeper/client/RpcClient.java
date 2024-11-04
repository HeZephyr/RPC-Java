package rpc.zookeeper.client;

import rpc.zookeeper.common.message.RpcRequest;
import rpc.zookeeper.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
