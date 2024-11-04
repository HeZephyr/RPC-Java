package rpc.netty.client.netty.initializer;

import com.esotericsoftware.kryo.Kryo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import rpc.netty.client.netty.handler.NettyClientHandler;
import rpc.netty.common.codec.KryoDecoder;
import rpc.netty.common.codec.KryoEncoder;
import rpc.netty.common.message.RpcRequest;
import rpc.netty.common.message.RpcResponse;

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
