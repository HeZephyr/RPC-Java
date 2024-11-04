package rpc.zookeeper.client.netty.initializer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import rpc.zookeeper.client.netty.handler.NettyClientHandler;
import rpc.zookeeper.common.codec.KryoDecoder;
import rpc.zookeeper.common.codec.KryoEncoder;
import rpc.zookeeper.common.message.RpcResponse;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Length-based frame decoder to handle packet fragmentation and reassembly
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));

        // Kryo-based custom encoder and decoder for efficient object serialization
        pipeline.addLast(new KryoEncoder());
        pipeline.addLast(new KryoDecoder(RpcResponse.class));

        // Custom RPC server handler to process incoming RPC requests
        pipeline.addLast(new NettyClientHandler());
    }
}
