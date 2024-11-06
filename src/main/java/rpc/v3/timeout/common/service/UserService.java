package rpc.v3.timeout.common.service;

import rpc.v3.timeout.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
