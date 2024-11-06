package rpc.v3.timeout.client.discovery.balance.impl;

import rpc.v3.timeout.client.discovery.balance.LoadBalance;

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
