package rpc.v3.balancing.common.serializer.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v3.balancing.common.message.RpcMessages;
import rpc.v3.balancing.common.serializer.Serializer;

public class ProtoBufSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(ProtoBufSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        logger.info("Serializing object of type: {}", object.getClass().getName());
        if (object instanceof RpcMessages.RpcRequest) {
            logger.info("Serializing RpcRequest object");
            return ((RpcMessages.RpcRequest) object).toByteArray();
        } else if (object instanceof RpcMessages.RpcResponse) {
            logger.info("Serializing RpcResponse object");
            return ((RpcMessages.RpcResponse) object).toByteArray();
        } else {
            logger.error("Unsupported object type for serialization");
            throw new IllegalArgumentException("Unsupported object type for serialization");
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        logger.info("Deserializing message with type: {}", messageType);
        try {
            if (messageType == 0) {
                return RpcMessages.RpcRequest.parseFrom(bytes);
            } else if (messageType == 1) {
                return RpcMessages.RpcResponse.parseFrom(bytes);
            } else {
                throw new IllegalArgumentException("Unsupported message type for deserialization");
            }
        } catch (InvalidProtocolBufferException e) {
            logger.error("Failed to deserialize Protobuf message");
            throw new RuntimeException("Failed to deserialize Protobuf message", e);
        }
    }

    @Override
    public int getType() {
        return 2;
    }
}