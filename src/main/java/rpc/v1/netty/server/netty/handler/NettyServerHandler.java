package rpc.v1.netty.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import rpc.v1.netty.server.provider.ServiceProvider;
import rpc.v1.netty.common.message.RpcRequest;
import rpc.v1.netty.common.message.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = Logger.getLogger(NettyServerHandler.class.getName());

    private final ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.info("Server received the request: " + rpcRequest);
        // accept the request, process it, and return the result
        RpcResponse rpcResponse = handleRequest(rpcRequest);
        channelHandlerContext.writeAndFlush(rpcResponse);
        channelHandlerContext.close();
    }

    private RpcResponse handleRequest(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getInterfaceName());
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object invoke = method.invoke(service, rpcRequest.getParameters());
            logger.info("Service method invoked successfully.");
            return RpcResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "Error invoking service method", e);
            return RpcResponse.fail(500, "Error invoking service method");
        }
    }
}
