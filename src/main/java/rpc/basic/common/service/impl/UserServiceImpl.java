package rpc.basic.common.service.impl;

import rpc.basic.common.pojo.User;
import rpc.basic.common.service.UserService;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the UserService interface, providing methods
 * to retrieve a user by ID and insert a user.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private static final Random random = new Random();

    @Override
    public User getUserById(Integer id) {
        logger.log(Level.INFO, "Retrieving user with ID: {0}", id);

        User user = User.builder()
                .id(id)
                .name("User" + id)
                .sex(random.nextBoolean())
                .build();

        logger.log(Level.INFO, "User retrieved: {0}", user);
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        logger.log(Level.INFO, "Inserting user: {0}", user);

        Integer userId = user.getId();
        logger.log(Level.INFO, "User inserted with ID: {0}", userId);

        return userId;
    }
}
