package rpc.v4.limiter.common.service;

import rpc.v4.limiter.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
