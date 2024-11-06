package rpc.v4.circuitbreaker.client.discovery.balance.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.circuitbreaker.client.discovery.balance.LoadBalance;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {
    private static final Logger logger = LoggerFactory.getLogger(RandomLoadBalance.class);
    @Override
    public String balance(List<String> addressList) {
        int size = addressList.size();
        int index = new Random().nextInt(size);
        return addressList.get(index);
    }
    @Override
    public void addNode(String address) {
        // do nothing
    }

    @Override
    public void removeNode(String address) {
        // do nothing
    }
}
