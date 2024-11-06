package rpc.v4.circuitbreaker.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.circuitbreaker.common.message.MessageType;
import rpc.v4.circuitbreaker.common.message.RpcRequest;
import rpc.v4.circuitbreaker.common.message.RpcResponse;
import rpc.v4.circuitbreaker.common.serializer.Serializer;

@AllArgsConstructor
public class KryoEncoder extends MessageToByteEncoder<Object> {
    private static final Logger logger = LoggerFactory.getLogger(KryoEncoder.class);
    private final Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 1. write the message type
        if (msg instanceof RpcRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
            logger.info("Encoding RpcRequest message.");
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
            logger.info("Encoding RpcResponse message.");
        } else {
            logger.error("Unsupported message type: {}", msg.getClass().getName());
            throw new IllegalArgumentException("Unsupported message type: " + msg.getClass().getName());
        }
        // 2. write the serializer type
        out.writeShort(serializer.getType());
        logger.info("Serializer type: {}", serializer.getType());

        // 3. get the serialized byte array
        byte[] bytes = serializer.serialize(msg);
        logger.info("Serialized message: {}", msg);

        // 4. write the length of the serialized byte array
        out.writeInt(bytes.length);
        logger.info("Serialized message size: {} bytes", bytes.length);

        // 5. write the serialized byte array
        out.writeBytes(bytes);
        logger.info("Message encoded and written to ByteBuf.");
    }
}
