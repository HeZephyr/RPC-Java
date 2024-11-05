package rpc.v2.cache.client.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v2.cache.client.RpcClient;
import rpc.v2.cache.client.discovery.impl.ZKServiceDiscovery;
import rpc.v2.cache.client.netty.initializer.NettyClientInitializer;
import rpc.v2.cache.common.message.RpcRequest;
import rpc.v2.cache.common.message.RpcResponse;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyRpcClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    // Netty Bootstrap and EventLoopGroup for managing connections
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    // Service discovery component to find service addresses from ZooKeeper
    private final ZKServiceDiscovery serviceDiscovery;

    static {
        // Initialize the EventLoopGroup and Bootstrap for Netty
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());  // Set up the client initializer
    }

    // Constructor to initialize service discovery
    public NettyRpcClient() {
        this.serviceDiscovery = new ZKServiceDiscovery();
    }

    /**
     * Sends an RPC request to the server and waits for the response.
     *
     * @param request The RPC request to be sent
     * @return The RPC response received from the server
     */
    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        // Discover the service address (host and port) based on the service name
        String serviceName = request.getInterfaceName();
        InetSocketAddress serviceAddress = serviceDiscovery.discoveryService(serviceName);
        String host = serviceAddress.getHostName();
        int port = serviceAddress.getPort();

        try {
            // Connect to the server using the discovered host and port
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();

            // Set up an attribute key for the response to be stored in the channel
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");

            // Send the request and add a listener to handle send success or failure
            channel.writeAndFlush(request).addListener(future -> {
                if (future.isSuccess()) {
                    logger.info("Request sent successfully to {}:{}", host, port);
                } else {
                    logger.error("Failed to send request to {}:{}", host, port);
                    channel.close();  // Close the channel if sending fails
                }
            });

            // Wait for the response with a timeout
            boolean completed = channel.closeFuture().await(10, TimeUnit.SECONDS);
            if (!completed) {
                logger.error("Request timed out waiting for response from {}:{}", host, port);
                return RpcResponse.fail(500, "Request timed out");
            }

            // Retrieve the response from the channel attribute
            RpcResponse response = channel.attr(key).get();
            logger.info("Received response: {}", response);

            // Return the response, or a failure response if no response was received
            return response != null ? response : RpcResponse.fail(500, "No response from server");

        } catch (InterruptedException e) {
            // Log the error and re-interrupt the thread to handle InterruptedException properly
            logger.error("An error occurred while sending the request to {}:{}", host, port, e);
            Thread.currentThread().interrupt();
            return RpcResponse.fail(500, "An error occurred while sending the request");
        }
    }
}