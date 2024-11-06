package rpc.v4.circuitbreaker.client.discovery.balance;

import java.util.List;

public interface LoadBalance {
    String balance(List<String> addressList);
    void addNode(String address);
    void removeNode(String address);
}
