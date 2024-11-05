package rpc.v1.zookeeper.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v1.zookeeper.server.provider.ServiceProvider;
import rpc.v1.zookeeper.common.message.RpcRequest;
import rpc.v1.zookeeper.common.message.RpcResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    // Service provider to manage service instances
    private final ServiceProvider serviceProvider;

    /**
     * Handles an incoming RPC request by processing it and sending back a response.
     *
     * @param channelHandlerContext the context of the channel
     * @param rpcRequest            the RPC request received from the client
     * @throws Exception if any error occurs during request processing
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.info("Server received request: {}", rpcRequest);

        // Process the request and get the response
        RpcResponse rpcResponse = handleRequest(rpcRequest);

        // Send the response back to the client
        channelHandlerContext.writeAndFlush(rpcResponse);

        // Close the connection after responding
        channelHandlerContext.close();
    }

    /**
     * Handles the RPC request by invoking the corresponding service method.
     *
     * @param rpcRequest the RPC request containing method information
     * @return RpcResponse containing the result of the method invocation or an error
     */
    private RpcResponse handleRequest(RpcRequest rpcRequest) {
        // Retrieve the service instance based on the interface name
        Object service = serviceProvider.getService(rpcRequest.getInterfaceName());

        try {
            // Find the method in the service class that matches the request
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());

            // Invoke the method with the provided parameters
            Object result = method.invoke(service, rpcRequest.getParameters());
            logger.info("Service method invoked successfully.");

            // Return a successful response with the result
            return RpcResponse.success(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error invoking service method", e);
            return RpcResponse.fail(500, "Error invoking service method");
        }
    }
}