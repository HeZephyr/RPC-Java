package rpc.v2.custom.common.service;

import rpc.v2.custom.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
