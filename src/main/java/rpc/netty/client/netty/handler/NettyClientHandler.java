package rpc.netty.client.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import rpc.basic.server.provider.ServiceProvider;
import rpc.netty.common.message.RpcRequest;
import rpc.netty.common.message.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logger logger = Logger.getLogger(NettyClientHandler.class.getName());
        logger.log(Level.SEVERE, "client caught exception", cause);
        ctx.close();
    }
}
