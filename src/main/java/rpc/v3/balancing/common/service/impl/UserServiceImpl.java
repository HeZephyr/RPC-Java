package rpc.v3.balancing.common.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v3.balancing.common.pojo.User;
import rpc.v3.balancing.common.service.UserService;

import java.util.Random;

/**
 * Implementation of the UserService interface, providing methods
 * to retrieve a user by ID and insert a user.
 */
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final Random random = new Random();

    /**
     * Retrieves a user by the given ID.
     *
     * @param id the ID of the user to retrieve
     * @return a User object populated with sample data based on the provided ID
     */
    @Override
    public User getUserById(Integer id) {
        logger.info("Retrieving user with ID: {}", id);

        // Create a User object with sample data
        User user = User.builder()
                .id(id)
                .name("User" + id)
                .sex(random.nextBoolean())  // Randomly assign gender
                .build();

        logger.info("User retrieved: {}", user);
        return user;
    }

    /**
     * Inserts a user and returns the ID of the inserted user.
     *
     * @param user the User object to insert
     * @return the ID of the inserted user
     */
    @Override
    public Integer insertUserId(User user) {
        logger.info("Inserting user: {}", user);

        // For this example, we simply return the user's ID as confirmation of insertion
        Integer userId = user.getId();
        logger.info("User inserted with ID: {}", userId);

        return userId;
    }
}