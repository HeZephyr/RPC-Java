package rpc.v1.netty.server.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import rpc.v1.netty.server.RpcServer;
import rpc.v1.netty.server.netty.initializer.NettyServerInitializer;
import rpc.v1.netty.server.provider.ServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private final Logger logger = Logger.getLogger(NettyRpcServer.class.getName());
    private ServiceProvider serviceProvider;
    @Override
    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        logger.info("Starting server...");
        try {
            // Create a new server instance
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceProvider))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections
            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.info("Server started successfully on port " + port);
            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.severe("Error occurred during server start: " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * Stops the server by setting the running flag to false.
     * This allows the main server loop to exit, stopping the acceptance of new connections.
     */
    @Override
    public void stop() {
        logger.log(Level.INFO, "Server is stopping...");
    }
}
