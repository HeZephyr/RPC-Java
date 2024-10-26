package rpc.basic.client.transport;

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
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            // Send the request to the server
            outputStream.writeObject(request);
            outputStream.flush();

            // Read the response from the server and return it
            return (RpcResponse) inputStream.readObject();

        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O error occurred while sending RPC request", e);
            // Return a failure response with a 500 status code and a message for I/O errors
            return RpcResponse.fail(500, "I/O error occurred while sending RPC request");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Class not found during RPC request deserialization", e);
            // Return a failure response with a 400 status code and a message for class not found errors
            return RpcResponse.fail(400, "Class not found during RPC request deserialization");
        }
    }
}
