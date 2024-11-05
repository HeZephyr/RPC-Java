package rpc.v1.zookeeper.server;

/**
 * RpcServer interface defines the basic operations for starting and stopping an RPC server.
 */
public interface RpcServer {
    /**
     * Starts the RPC server and binds it to the specified port.
     * @param port the port on which the server should listen
     */
    void start(int port);
    /**
     * Stops the RPC server and releases any resources associated with it.
     */
    void stop();
}
