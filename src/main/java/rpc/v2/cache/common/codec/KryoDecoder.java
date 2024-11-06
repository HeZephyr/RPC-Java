package rpc.v2.cache.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v2.cache.common.message.MessageType;
import rpc.v2.cache.common.serializer.Serializer;

import java.util.List;

public class KryoDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(KryoDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. read the message type
        short messageType = in.readShort();
        if (messageType != MessageType.REQUEST.getCode() && messageType != MessageType.RESPONSE.getCode()) {
            logger.error("Unsupported message type: {}", messageType);
            return;
        }

        // 2. read the serializer type
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializer(serializerType);
        if (serializer == null) {
            logger.error("Unsupported serializer type: {}", serializerType);
            throw new RuntimeException("Unsupported serializer type: " + serializerType);
        }

        // 3. read the length of the serialized byte array
        int length = in.readInt();

        // 4. read the serialized byte array
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 5. use the specified serializer to deserialize the byte array
        Object deserializedObject = serializer.deserialize(bytes, messageType);
        out.add(deserializedObject);

        logger.info("Decoded message of type {} using serializer type {}", messageType, serializerType);
    }
}
