package rpc.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rpc.basic.client.proxy.ClientProxy;
import rpc.basic.common.pojo.User;
import rpc.basic.common.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SimpleRpcClientTest {

    private UserService proxy;

    @BeforeEach
    public void setup() {
        // Initialize ClientProxy and get the UserService proxy
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        proxy = clientProxy.getProxy(UserService.class);
    }

    @Test
    public void testGetUserById() {
        // Test retrieval of user from the server
        User user = proxy.getUserById(1);

        // Assertions to verify correctness of user data
        assertNotNull(user, "User should not be null");
        System.out.println("User from server: " + user);

        // Additional assertions based on expected properties of 'user'
        assertEquals(1, user.getId(), "User ID should be 1");
    }

    @Test
    public void testInsertUserId() {
        // Create a new User instance to send to the server
        User user = User.builder().id(100).name("wxx").sex(true).build();

        // Insert user and retrieve the ID from the server
        Integer id = proxy.insertUserId(user);

        // Assertions to verify the ID was received as expected
        assertNotNull(id, "Returned ID should not be null");
        assertEquals(100, id, "Inserted user ID should match the expected ID");
        System.out.println("Inserted user ID: " + id);
    }
}
