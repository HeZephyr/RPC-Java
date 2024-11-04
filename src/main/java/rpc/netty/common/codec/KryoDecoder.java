package rpc.netty.common.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpc.netty.common.message.RpcRequest;
import rpc.netty.common.message.RpcResponse;
import rpc.netty.common.pojo.User;

import java.util.List;

public class KryoDecoder extends ByteToMessageDecoder {
    private final Kryo kryo;
    private final Class<?> clazz;

    public KryoDecoder(Class<?> clazz) {
        kryo = new Kryo();
        this.clazz = clazz;
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.register(Class[].class);       // register Class[] type
        kryo.register(Object[].class);      // register Object[] type
        kryo.register(User.class);          // register User type
        kryo.register(Class.class);         // register Class type
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try (ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
             Input input = new Input(byteBufInputStream)) {
            Object obj = kryo.readObject(input, clazz);
            out.add(obj);
        }
    }
}
