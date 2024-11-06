package rpc.v4.circuitbreaker.client.discovery.watcher;

import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.circuitbreaker.client.cache.ServiceCache;

@AllArgsConstructor
public class ZKWatcher {
    private CuratorFramework client;
    private ServiceCache serviceCache;
    private final static Logger logger = LoggerFactory.getLogger(ZKWatcher.class);

    // Watch the service node and update the service cache
    public void watchNode(String path) {
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData newData) {
                switch (type.name()) {
                    case "NODE_CREATED" -> { // when a new node is created
                        String[] pathList = parsePath(newData);
                        if (pathList.length > 2) {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            serviceCache.addServiceAddress(serviceName, address);
                        }
                    }
                    case "NODE_CHANGED" -> { // when a node is updated
                        logger.info("before update: " + new String(oldData.getData()));
                        String[] oldPathList = parsePath(oldData);
                        String[] newPathList = parsePath(newData);
                        if (oldPathList.length > 2 && newPathList.length > 2) {
                            String serviceName = oldPathList[1];
                            String oldAddress = oldPathList[2];
                            String newAddress = newPathList[2];
                            serviceCache.updateServiceAddress(serviceName, oldAddress, newAddress);
                            logger.info("after update: " + new String(newData.getData()));
                        }
                    }
                    case "NODE_DELETED" -> {
                        String[] pathList = parsePath(oldData);
                        if (pathList.length > 2) {
                            String serviceName = pathList[1];
                            String address = pathList[2];
                            serviceCache.removeServiceAddress(serviceName, address);
                        }
                    }
                    default -> logger.info("Other event: " + type.name());
                }
            }
        });
        curatorCache.start();
    }

    public String[] parsePath(ChildData childData) {
        String path = childData.getPath();
        return path.split("/");
    }
}
