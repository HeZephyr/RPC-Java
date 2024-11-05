package rpc.v1.zookeeper.client;

import rpc.v1.zookeeper.common.message.RpcRequest;
import rpc.v1.zookeeper.common.message.RpcResponse;

public interface RpcClient {
    RpcResponse sendRequest(RpcRequest request);
}
