package rpc.netty.server.netty.initializer;

import com.esotericsoftware.kryo.Kryo;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.AllArgsConstructor;
import rpc.netty.server.provider.ServiceProvider;
import rpc.netty.common.codec.KryoDecoder;
import rpc.netty.common.codec.KryoEncoder;
import rpc.netty.common.message.RpcRequest;
import rpc.netty.common.message.RpcResponse;
import rpc.netty.server.netty.handler.NettyServerHandler;

/**
 * NettyServerInitializer is responsible for setting up the channel pipeline
 * for the Netty RPC server. It configures the necessary encoders, decoders,
 * and custom handlers for handling RPC requests.
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final ServiceProvider serviceProvider;

    /**
     * Initializes the channel by adding handlers to the pipeline, including length-based frame decoder
     * for packet fragmentation, a length field prepender, Kryo-based encoder, decoder, and the custom RPC server handler.
     *
     * @param ch the SocketChannel to be initialized
     */
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // Initialize Kryo and register classes to be serialized

        // Length-based frame decoder to handle packet fragmentation and reassembly
        pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast(new LengthFieldPrepender(4));

        // Add Kryo encoder and decoder
        pipeline.addLast(new KryoEncoder());
        pipeline.addLast(new KryoDecoder(RpcRequest.class));

        // Custom RPC server handler to process incoming RPC requests
        pipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
