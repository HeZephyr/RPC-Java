package rpc.v1.basic.server.impl;

import rpc.v1.basic.server.RpcServer;
import rpc.v1.basic.server.provider.ServiceProvider;
import rpc.v1.basic.server.work.WorkerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SimpleRpcServer is a basic implementation of the RpcServer interface,
 * responsible for accepting client connections and handling RPC requests.
 * This server listens on a specified port and processes each client request
 * using a separate WorkerThread.
 */
public class SimpleRpcServer implements RpcServer {

    private static final Logger logger = Logger.getLogger(SimpleRpcServer.class.getName());

    // The service provider that registers and provides the available services for clients
    private final ServiceProvider serviceProvider;

    // Flag to control the running state of the server, allowing for safe shutdown
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    /**
     * Constructs a new SimpleRpcServer with the specified ServiceProvider.
     *
     * @param serviceProvider the service provider that registers and manages available services
     */
    public SimpleRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * Starts the server on the specified port, listening for client connections.
     * For each new connection, a new WorkerThread is spawned to handle the client's request.
     *
     * @param port the port on which the server should listen for incoming connections
     */
    @Override
    public void start(int port) {
        logger.log(Level.INFO, "Server started on port {0}", port);
        // Create a ServerSocket to listen on the specified port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // Keep accepting connections while the server is running
            while (isRunning.get()) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, "Client connected: {0}", socket.getRemoteSocketAddress());

                // Spawn a new thread to handle the client request using WorkerThread
                new Thread(new WorkerThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error starting server on port " + port, e);
        }
    }

    /**
     * Stops the server by setting the running flag to false.
     * This allows the main server loop to exit, stopping the acceptance of new connections.
     */
    @Override
    public void stop() {
        isRunning.set(false);
        logger.log(Level.INFO, "Server is stopping...");
    }
}
