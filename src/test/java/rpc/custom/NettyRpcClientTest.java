package rpc.custom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rpc.custom.client.proxy.ClientProxy;
import rpc.custom.common.pojo.User;
import rpc.custom.common.service.UserService;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for RPC client functionality with UserService.
 */
public class NettyRpcClientTest {

    private static final Logger logger = Logger.getLogger(NettyRpcClientTest.class.getName());
    private UserService proxy;

    @BeforeEach
    public void setup() {
        // Initialize the ClientProxy and create a UserService proxy instance
        ClientProxy clientProxy = new ClientProxy();
        proxy = clientProxy.getProxy(UserService.class);
        logger.log(Level.INFO, "ClientProxy initialized for UserService with proxy instance created.");
    }

    @Test
    public void testGetUserByUserId() {
        // Retrieve a user from the server with a specific user ID
        User user = proxy.getUserById(1);

        // Validate that the retrieved user is not null and has the expected properties
        assertNotNull(user, "User should not be null");
        assertEquals(1, user.getId(), "User ID should be 1");

        logger.log(Level.INFO, "User retrieved from server: {0}", user);
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

        logger.log(Level.INFO, "Inserted user ID: {0}", id);
    }
}
