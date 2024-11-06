package rpc.v2.custom.client.netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import rpc.v2.custom.client.netty.handler.NettyClientHandler;
import rpc.v2.custom.common.codec.KryoDecoder;
import rpc.v2.custom.common.codec.KryoEncoder;
import rpc.v2.custom.common.serializer.impl.JsonSerializer;

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
