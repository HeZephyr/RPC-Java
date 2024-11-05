package rpc.v3.balancing.common.service;

import rpc.v3.balancing.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
