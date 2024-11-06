package rpc.v4.limiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.v4.limiter.client.proxy.ClientProxy;
import rpc.v4.limiter.common.pojo.User;
import rpc.v4.limiter.common.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for RPC client functionality with UserService.
 */
public class NettyRpcClientTest {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientTest.class);
    private UserService proxy;

    @BeforeEach
    public void setup() {
        // Initialize the ClientProxy and create a UserService proxy instance
        ClientProxy clientProxy = new ClientProxy();
        proxy = clientProxy.getProxy(UserService.class);
        logger.info("ClientProxy initialized for UserService with proxy instance created.");
    }

    @Test
    public void testGetUserByUserId() {
        // Retrieve a user from the server with a specific user ID
        User user = proxy.getUserById(1);

        // Validate that the retrieved user is not null and has the expected properties
        assertNotNull(user, "User should not be null");
        assertEquals(1, user.getId(), "User ID should be 1");

        logger.info("User retrieved from server: {}", user);
    }

    @Test
    public void testInsertUserId() {
        // Create a new User instance to send to the server
        User user = User.builder().id(100).name("wxx").sex(true).build();

        // Insert the user and get the ID from the server response
        Integer id = proxy.insertUserId(user);

        // Validate that the returned ID is not null and matches the expected ID
        assertNotNull(id, "Returned ID should not be null");
        assertEquals(100, id, "Inserted user ID should match the expected ID");

        logger.info("Inserted user ID: {}", id);
    }
}