package rpc.v1.netty.client.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import rpc.v1.netty.client.RpcClient;
import rpc.v1.netty.client.netty.initializer.NettyClientInitializer;
import rpc.v1.netty.common.message.RpcRequest;
import rpc.v1.netty.common.message.RpcResponse;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyRpcClient implements RpcClient {
    private static final Logger logger = Logger.getLogger(NettyRpcClient.class.getName());
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            logger.info("Connecting to the server " + host + ":" + port);

            // Connect to the server
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();

            // Set up an attribute key for the response
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");

            // Send the request
            channel.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("Request sent successfully.");
                } else {
                    logger.severe("Failed to send request: " + future.cause());
                    channel.close();
                }
            });

            // Wait for the response to be set in the channel attribute
            channel.closeFuture().await(10, TimeUnit.SECONDS);  // Set a timeout if desired
            RpcResponse response = channel.attr(key).get();

            logger.info("Received response: " + response);
            return response != null ? response : RpcResponse.fail(500, "No response from server");

        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "An error occurred while sending the request: ", e);
            Thread.currentThread().interrupt();
            return RpcResponse.fail(500, "An error occurred while sending the request");
        }
    }
}
