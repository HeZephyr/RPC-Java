package rpc.netty.client.transport;

import rpc.basic.common.message.RpcRequest;
import rpc.basic.common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * IOClient is responsible for handling the low-level communication with the server,
 * sending RPC requests, and returning RPC responses.
 */
public class IOClient {

    private static final Logger logger = Logger.getLogger(IOClient.class.getName());

    /**
     * Sends an RPC request to the specified host and port, and returns the RPC response.
     *
     * @param host    the host to connect to
     * @param port    the port to connect to
     * @param request the RPC request to send
     * @return the RPC response from the server
     */
    public static RpcResponse sendRequest(String host, int port, RpcRequest request) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(request);
            objectOutputStream.flush();

            return (RpcResponse) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "An error occurred while sending the request", e);
            return RpcResponse.fail(500, "An error occurred while sending the request");
        }
    }
}
