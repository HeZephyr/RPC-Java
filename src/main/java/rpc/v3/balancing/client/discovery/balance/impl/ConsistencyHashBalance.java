package rpc.v3.balancing.client.discovery.balance.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v3.balancing.client.discovery.balance.LoadBalance;

import java.util.*;

/**
 * Consistency Hash Load Balancer with Virtual Nodes
 */
public class ConsistencyHashBalance implements LoadBalance {
    private static final Logger logger = LoggerFactory.getLogger(ConsistencyHashBalance.class);
    private static final int VIRTUAL_NUM = 5; // Number of virtual nodes per real node

    private final SortedMap<Integer, String> shards = new TreeMap<>(); // Map for virtual node hash values
    private final List<String> realNodes = new LinkedList<>(); // List of real nodes

    /**
     * Initialize the hash ring and add virtual nodes for each real node.
     */
    private void init(List<String> serviceList) {
        for (String server : serviceList) {
            addNode(server);
        }
    }

    /**
     * Get the closest server for the given node key.
     */
    public String getServer(String node, List<String> serviceList) {
        init(serviceList);
        int hash = getHash(node);
        SortedMap<Integer, String> subMap = shards.tailMap(hash);
        int key = subMap.isEmpty() ? shards.firstKey() : subMap.firstKey();
        String virtualNode = shards.get(key);
        return virtualNode.substring(0, virtualNode.indexOf("&&"));
    }

    /**
     * Add a new real node along with its virtual nodes to the hash ring.
     */
    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)) {
            realNodes.add(node);
            logger.info("Real node [{}] added", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                logger.info("Virtual node [{}] with hash {} added", virtualNode, hash);
            }
        }
    }

    /**
     * Remove a real node and its virtual nodes from the hash ring.
     */
    @Override
    public void removeNode(String node) {
        if (realNodes.remove(node)) {
            logger.info("Real node [{}] removed", node);
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                logger.info("Virtual node [{}] with hash {} removed", virtualNode, hash);
            }
        }
    }

    /**
     * FNV1_32_HASH hash function.
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        return Math.abs(hash);
    }

    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        return getServer(random, addressList);
    }
}