# ZookeeperRPC Implementation Overview

This document provides a comprehensive overview of the ZooKeeper-based RPC framework, detailing the client-server architecture, core components, and the integration of ZooKeeper for service discovery. This framework enables dynamic service registration and lookup, improving the scalability and flexibility of the RPC system.

## Table of Contents
1. [Introduction](#introduction)
2. [Architecture Overview](#architecture-overview)
3. [Core Components](#core-components)
4. [Workflow](#workflow)
5. [Benefits of Using ZooKeeper](#benefits-of-using-zookeeper)
6. [Future Enhancements](#future-enhancements)

---

### 1. Introduction

The ZookeeperRPC framework extends the basic RPC architecture by incorporating **ZooKeeper** for dynamic service discovery. ZooKeeper allows servers to register their services at runtime and enables clients to discover available services dynamically. This integration enhances the framework’s flexibility and resilience, as clients can automatically connect to new service instances without needing hardcoded IP addresses.

### 2. Architecture Overview

The architecture of the ZookeeperRPC framework includes:
- **Service Provider**: Registers services with ZooKeeper when they come online, storing the host and port for each service instance.
- **Service Register (ZooKeeper)**: Acts as a service registry, storing the addresses of service instances and notifying clients of service changes.
- **Service Discovery**: Clients use ZooKeeper to look up service instances dynamically, allowing for load balancing and high availability.
- **ZooKeeper Nodes**: Stores registered services as temporary nodes, which are removed if a service instance goes offline.

### 3. Core Components

#### 3.1 `ServiceProvider`
Located at: `src/main/java/rpc/zookeeper/server/provider/ServiceProvider.java`

- **Purpose**: Manages service instances and registers them with ZooKeeper for discovery by clients.
- **Key Methods**:
    - `provideServiceInterface`: Registers a service instance with ZooKeeper, creating a temporary node under the service path.
    - `getService`: Retrieves the service instance by the interface name, used for client requests.

#### 3.2 `ZKServiceRegister`
Located at: `src/main/java/rpc/zookeeper/server/register/impl/ZKServiceRegister.java`

- **Purpose**: Handles the ZooKeeper connection and manages the creation of nodes for service registration.
- **Key Methods**:
    - `register`: Registers a service instance by creating a temporary ZooKeeper node with the service address.
    - `init`: Initializes the connection to the ZooKeeper server and prepares the root path for service registration.

#### 3.3 `ZKServiceDiscovery`
Located at: `src/main/java/rpc/zookeeper/client/discovery/impl/ZKServiceDiscovery.java`

- **Purpose**: Allows clients to look up available services by querying ZooKeeper for service instances.
- **Key Methods**:
    - `discoveryService`: Retrieves the list of available instances for a given service and performs load balancing by randomly selecting one instance.

#### 3.4 ZooKeeper Temporary and Persistent Nodes
Located at: `ZooKeeper` server configuration

ZooKeeper nodes are used to register and discover services:
- **Persistent Nodes**: The root node, `/services`, is a persistent node that holds each service as a sub-node.
- **Temporary Nodes**: Each service instance creates a temporary node under its service name, which is automatically deleted if the instance goes offline.

### 4. Workflow

The following steps outline the overall workflow in the ZookeeperRPC framework:

```
                        +-----------------------------+
                        |      ZooKeeper Cluster      |
                        +-----------------------------+
                         |                            |
                 Service Registration            Service Discovery
                         |                            |
                         v                            v
+-------------------+          1. Client connects to ZooKeeper    +-------------------+
| Service Provider | <-----------------------------------------> | Service Discovery |
+-------------------+          2. Client queries service nodes    +-------------------+
       |                 to retrieve available instances               |
       |                                                          |
       |                                                          v
1. Connect to ZooKeeper                                   3. Load balancing
2. Create persistent root node                             4. Client selects instance
   (e.g., `/services`)                                    5. Client calls method on selected instance
3. Register temporary node for
   each instance (e.g., `/services/ServiceA/instance1`)
4. When instance goes offline, ZooKeeper automatically
   removes its node
```
1. **Service Registration**:
    - When a service instance starts, `ServiceProvider` registers it with ZooKeeper by creating a temporary node under the corresponding service path (e.g., `/services/ServiceA/instance1`).

2. **Client Service Discovery**:
    - The client connects to ZooKeeper to look up the address of a service by name (e.g., `ServiceA`), retrieving the list of available instances under `/services/ServiceA`.

3. **Dynamic Load Balancing**:
    - The client randomly selects one of the service instances to distribute the load. Alternatively, more advanced load balancing strategies can be implemented.

4. **Client Monitoring of Service Availability**:
    - The client registers a watch on the service node (e.g., `/services/ServiceA`). If a service instance goes offline or a new one is added, ZooKeeper notifies the client, allowing it to update the list of available instances.

### 5. Benefits of Using ZooKeeper

Integrating ZooKeeper into the RPC framework provides several benefits:

- **Dynamic Service Management**: Services can register and deregister dynamically, and clients automatically adapt to service availability changes.
- **High Availability**: Clients can discover new service instances at runtime, reducing the risk of a single point of failure.
- **Consistent Data**: ZooKeeper ensures consistent service registration data across nodes, improving reliability.
- **Real-Time Updates**: Clients can monitor service changes with watches, receiving real-time notifications when service nodes are added or removed.
- **Efficient Load Balancing**: Clients can use ZooKeeper’s list of service instances to implement various load-balancing strategies.

### 6. Future Enhancements

Potential improvements for the ZookeeperRPC framework include:

- **Advanced Load Balancing**: Implement load balancing algorithms like round-robin, consistent hashing, or weighted balancing to improve distribution.
- **Caching and Retry Mechanism**: Introduce caching for frequently accessed services and retry mechanisms for improved fault tolerance.
- **Enhanced Security**: Secure the communication between services and ZooKeeper, and implement access control to protect sensitive data.
- **Service Health Checks**: Integrate health checks to proactively monitor and remove unhealthy service instances from ZooKeeper.
- **Metrics and Monitoring**: Track request counts, response times, and resource usage for monitoring and scaling purposes.