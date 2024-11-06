package rpc.v1.basic.server.impl;

import rpc.v1.basic.server.RpcServer;
import rpc.v1.basic.server.provider.ServiceProvider;
import rpc.v1.basic.server.work.WorkerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ThreadPoolRpcServer is an implementation of the RpcServer interface, using a thread pool
 * to handle multiple client requests concurrently. The server listens on a specified port,
 * accepts incoming client connections, and processes each request in a separate thread.
 */
public class ThreadPoolRpcServer implements RpcServer {

    private static final Logger logger = Logger.getLogger(ThreadPoolRpcServer.class.getName());

    // Thread pool to handle client requests concurrently
    private final ThreadPoolExecutor threadPoolExecutor;

    // Service provider to manage and provide registered services
    private final ServiceProvider serviceProvider;

    // Flag to control the running state of the server, allowing for safe shutdown
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    /**
     * Constructs a new ThreadPoolRpcServer with the specified ServiceProvider.
     *
     * @param serviceProvider the service provider that registers and manages available services
     */
    public ThreadPoolRpcServer(ServiceProvider serviceProvider) {
        this.threadPoolExecutor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                1000,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100)
        );
        this.serviceProvider = serviceProvider;
    }

    /**
     * Starts the server on the specified port, listening for client connections.
     * For each new connection, a WorkerThread is executed in the thread pool to handle the client's request.
     *
     * @param port the port on which the server should listen for incoming connections
     */
    @Override
    public void start(int port) {
        logger.log(Level.INFO, "Server started on port {0}", port);

        // Try-with-resources to automatically close the server socket on exit
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            threadPoolExecutor.prestartAllCoreThreads();

            // Accept connections while the server is running
            while (isRunning.get()) {
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, "Client connected: {0}", socket.getRemoteSocketAddress());

                // Execute the request in a separate thread using WorkerThread
                threadPoolExecutor.execute(new WorkerThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error starting server on port " + port, e);
        }
    }

    /**
     * Stops the server by setting the running flag to false and shutting down the thread pool.
     * This allows the main server loop to exit, stopping the acceptance of new connections.
     */
    @Override
    public void stop() {
        isRunning.set(false);
        threadPoolExecutor.shutdown();
        logger.log(Level.INFO, "Server is stopping...");
    }
}
