package rpc.basic.server.work;

import rpc.basic.common.message.RpcRequest;
import rpc.basic.common.message.RpcResponse;
import rpc.basic.server.provider.ServiceProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WorkThread handles client requests by processing RpcRequest objects,
 * invoking the corresponding service method, and sending back an RpcResponse.
 */
public class WorkerThread implements Runnable {

    private static final Logger logger = Logger.getLogger(WorkerThread.class.getName());
    private final Socket socket;
    private final ServiceProvider serviceProvider;

    public WorkerThread(Socket socket, ServiceProvider serviceProvider) {
        this.socket = socket;
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            // Read the RPC request from the client
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            logger.log(Level.INFO, "Received RPC request: {0}", rpcRequest);

            // Process the request and get the response
            RpcResponse rpcResponse = handleRequest(rpcRequest);

            // Send the response back to the client
            outputStream.writeObject(rpcResponse);
            outputStream.flush();
            logger.log(Level.INFO, "Sent RPC response: {0}", rpcResponse);

        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error handling client request", e);
        }
    }

    /**
     * Handles the RpcRequest by invoking the appropriate method on the service
     * and returns the RpcResponse.
     *
     * @param rpcRequest the RPC request received from the client
     * @return the RPC response to be sent back to the client
     */
    private RpcResponse handleRequest(RpcRequest rpcRequest) {
        String interfaceName = rpcRequest.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);

        if (service == null) {
            logger.log(Level.SEVERE, "Service not found for interface: {0}", interfaceName);
            return RpcResponse.fail(404, "Service not found");
        }

        try {
            // Retrieve the method to be invoked and invoke it
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            return RpcResponse.success(result);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "Error invoking service method", e);
            return RpcResponse.fail(500, "Error invoking service method");
        }
    }
}
