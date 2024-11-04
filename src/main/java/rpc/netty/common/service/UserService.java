package rpc.netty.common.service;

import rpc.netty.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
