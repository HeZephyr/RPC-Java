# RPC Project

This repository contains a custom-built RPC (Remote Procedure Call) framework in Java, divided into four versions. Each version progressively introduces new features such as caching, load balancing, retry mechanisms, rate limiting, and circuit breaking.

## Project Structure

The project is organized into four main versions (`v1`, `v2`, `v3`, `v4`), with each version implementing an enhanced set of features for the RPC framework. The core components of the framework include `Client`, `Server`, `Service Discovery`, `Load Balancer`, `Retry Mechanism`, `Rate Limiter`, and `Circuit Breaker`.

### Version Overview

- **v1**: Basic RPC framework with Netty for networking and Zookeeper for service discovery.
- **v2**: Introduces local caching of service addresses and custom serializers.
- **v3**: Adds load balancing algorithms and timeout retry mechanisms.
- **v4**: Implements rate limiting using the token bucket algorithm and circuit breaking for resilience.

## Architecture

The project is structured with separate packages for `client`, `server`, `discovery`, `common`, `netty`, and `ratelimit` functionalities. Each version builds upon the previous one by adding new components and features.

The architecture of this RPC framework is illustrated in the diagram below:
![image-20241106134021554](https://raw.githubusercontent.com/HeZephyr/NewPicGoLibrary/main/img/image-20241106134021554.png)


1. **Client**:
    - The client interacts with a **ClientProxy** object, which abstracts the RPC call.
    - The **Circuit Breaker** monitors request health, allowing or denying requests based on the failure rate.
    - The **Load Balancer** selects an appropriate server instance from a **Local Cache of Service Node Addresses**, which is populated through Zookeeper.
    - Requests are serialized and transmitted over TCP using **Netty**.
    - If a request times out, the **Retry Mechanism** attempts to resend it.

2. **Zookeeper**:
    - Acts as a **Service Registry** to facilitate service discovery and maintain a **Whitelist** for authorized services.

3. **Server**:
    - Registers itself with **Zookeeper** upon startup.
    - Incoming requests go through a **Rate Limiter** to prevent overload.
    - Requests are handled by **Netty** for decoding and deserialization.
    - The server processes the request and sends back the response via Netty.

## Features by Version

### v1 - Basic RPC

- **Components**:
    - `ClientProxy` for handling client-side requests.
    - Basic `RpcRequest` and `RpcResponse` message types.
    - `ServiceProvider` on the server for registering services.
    - Netty-based networking for serialization and deserialization.
    - Zookeeper for basic service discovery.

### v2 - Cache and Serialization

- **New Components**:
    - **ServiceCache**: Local cache to store discovered services.
    - **Serializers**: `JsonSerializer`, `ObjectSerializer`, and `ProtoBufSerializer` for message serialization.
    - `ZKServiceDiscovery` and `ZKWatcher` to monitor Zookeeper nodes for service updates.

### v3 - Load Balancing and Retry Mechanism

- **New Features**:
    - **Load Balancers**: Implements Consistent Hash, Random, and Round Robin load balancing strategies.
    - **Retry Mechanism**: Retry failed requests with configurable strategies (fixed wait, limited attempts).
    - **GuavaRetry**: Manages retries in case of network issues or server failures.

### v4 - Rate Limiting and Circuit Breaking

- **New Components**:
    - **Rate Limiter**: Uses a token bucket algorithm to limit request rates.
    - **Circuit Breaker**: Handles fault tolerance with states (OPEN, CLOSED, HALF-OPEN) based on failure thresholds.
    - **CircuitBreakerProvider**: Manages the circuit breaker for each client-service interaction.

## Package Structure

The main components are organized as follows:

```plaintext
rpc/
├── v1
│   ├── basic
│   ├── netty
│   └── zookeeper
├── v2
│   ├── cache
│   └── custom
├── v3
│   ├── balancing
│   └── timeout
└── v4
    ├── circuitbreaker
    └── limiter
```
Each version contains client, server, discovery, common, and netty packages to encapsulate respective functionalities.
