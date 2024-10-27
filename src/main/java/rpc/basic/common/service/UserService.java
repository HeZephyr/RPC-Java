package rpc.basic.common.service;

import rpc.basic.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
