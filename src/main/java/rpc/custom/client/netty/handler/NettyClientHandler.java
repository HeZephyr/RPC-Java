package rpc.custom.client.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.custom.common.message.RpcResponse;

/**
 * NettyClientHandler handles the RPC response received from the server.
 * It stores the response in a channel attribute and closes the channel afterward.
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    /**
     * Handles an incoming RpcResponse message by storing it in the channel's attributes.
     *
     * @param channelHandlerContext the context of the channel
     * @param rpcResponse           the RPC response received from the server
     * @throws Exception if any error occurs during processing
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        // Store the response in the channel attribute for retrieval by the client
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);

        // Close the channel after storing the response
        channelHandlerContext.channel().close();
    }

    /**
     * Handles exceptions that occur during communication.
     *
     * @param ctx   the context of the channel
     * @param cause the exception that was caught
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Log the exception and close the context
        logger.error("Client caught exception", cause);
        ctx.close();
    }
}