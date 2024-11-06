package rpc.v4.circuitbreaker.common.service;

import rpc.v4.circuitbreaker.common.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    Integer insertUserId(User user);
}
