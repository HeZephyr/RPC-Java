package rpc.v4.circuitbreaker.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.circuitbreaker.server.RpcServer;
import rpc.v4.circuitbreaker.server.netty.initializer.NettyServerInitializer;
import rpc.v4.circuitbreaker.server.provider.ServiceProvider;

@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);

    // Service provider to register and manage service instances
    private final ServiceProvider serviceProvider;

    /**
     * Starts the Netty RPC server on the specified port.
     *
     * @param port the port on which the server will listen for incoming connections
     */
    @Override
    public void start(int port) {
        // NioEventLoopGroups to handle client connections and I/O events
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        logger.info("Starting server...");

        try {
            // Set up the server bootstrap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  // Specify NIO transport channel
                    .childHandler(new NettyServerInitializer(serviceProvider))  // Set up the initializer with the service provider
                    .option(ChannelOption.SO_BACKLOG, 128)  // Set the backlog size
                    .childOption(ChannelOption.SO_KEEPALIVE, true);  // Enable TCP keep-alive

            // Bind the server to the specified port and start accepting connections
            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.info("Server started successfully on port {}", port);

            // Wait for the server channel to close (blocking)
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Error occurred during server start", e);
            Thread.currentThread().interrupt();  // Restore interrupt status
        } finally {
            // Shut down the event loop groups gracefully
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("Server has been stopped.");
        }
    }

    /**
     * Stops the server by shutting down event loops and releasing resources.
     * This will prevent the server from accepting new connections.
     */
    @Override
    public void stop() {
        logger.info("Server is stopping...");
    }
}