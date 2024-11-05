package rpc.custom.client.netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import rpc.custom.client.netty.handler.NettyClientHandler;
import rpc.custom.common.codec.KryoDecoder;
import rpc.custom.common.codec.KryoEncoder;
import rpc.custom.common.message.RpcResponse;
import rpc.custom.common.serializer.impl.JsonSerializer;
import rpc.custom.common.serializer.impl.ObjectSerializer;
import rpc.custom.common.serializer.impl.ProtoBufSerializer;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Length-based frame decoder to handle packet fragmentation and reassembly
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));

        // Kryo-based custom encoder and decoder for efficient object serialization
        pipeline.addLast(new KryoEncoder(new JsonSerializer()));
        pipeline.addLast(new KryoDecoder());

        // Custom RPC server handler to process incoming RPC requests
        pipeline.addLast(new NettyClientHandler());
    }
}
