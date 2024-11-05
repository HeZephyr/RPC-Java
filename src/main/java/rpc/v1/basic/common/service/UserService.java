package rpc.v1.basic.common.service;

import rpc.v1.basic.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
