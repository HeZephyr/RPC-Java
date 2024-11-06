package rpc.v1.netty.common.service;

import rpc.v1.netty.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
