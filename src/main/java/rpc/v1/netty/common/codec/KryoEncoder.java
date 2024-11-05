package rpc.v1.netty.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import rpc.v1.netty.common.message.RpcRequest;
import rpc.v1.netty.common.message.RpcResponse;
import rpc.v1.netty.common.pojo.User;

public class KryoEncoder extends MessageToByteEncoder<Object> {
    private final Kryo kryo;

    public KryoEncoder() {
        kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.register(Class[].class);       // register Class[] type
        kryo.register(Object[].class);      // register Object[] type
        kryo.register(User.class);          // register User type
        kryo.register(Class.class);         // register Class type

    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        try (ByteBufOutputStream byteBufOutputStream = new ByteBufOutputStream(out);
             Output output = new Output(byteBufOutputStream)) {
            kryo.writeObject(output, msg);
            output.flush();
        }
    }
}
