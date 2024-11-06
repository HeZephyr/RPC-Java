package rpc.v4.circuitbreaker.client.discovery.balance.impl;

import rpc.v4.circuitbreaker.client.discovery.balance.LoadBalance;

import java.util.List;

public class RoundLoadBalance implements LoadBalance {
    private int chooseIndex = 0;
    @Override
    public String balance(List<String> addressList) {
        if (chooseIndex >= addressList.size()) {
            chooseIndex = 0;
        }

        String address = addressList.get(chooseIndex);
        chooseIndex++;
        return address;
    }
    @Override
    public void addNode(String address) {
        // Do nothing
    }
    @Override
    public void removeNode(String address) {
        // Do nothing
    }
}
