package rpc.zookeeper.common.service;

import rpc.zookeeper.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
