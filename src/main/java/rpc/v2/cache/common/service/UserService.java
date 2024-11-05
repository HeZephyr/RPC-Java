package rpc.v2.cache.common.service;

import rpc.v2.cache.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
