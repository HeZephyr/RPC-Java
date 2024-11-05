package rpc.custom.common.service;

import rpc.custom.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
