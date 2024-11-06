package rpc.v1.zookeeper.common.service;

import rpc.v1.zookeeper.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
