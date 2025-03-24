package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    UserData testUser = new UserData("user", "pass", "email");

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        server.clear();
        facade.authToken = null;
    }

    @AfterAll
    static void stopServer() throws DataAccessException {
        server.clear();
        server.stop();
    }


    @Test
    @DisplayName("Register Positive")
    public void registerPositive() throws ResponseException {
        var auth = facade.register(testUser);
        assertEquals(auth.username(), testUser.username());
        assertEquals(auth.authToken(), facade.authToken);
        assertNotNull(facade.authToken);
    }

    @Test
    @DisplayName("Register Negative Already Taken")
    public void registerNegative() throws ResponseException {
        facade.register(testUser);
        assertThrows(ResponseException.class, () -> facade.register(testUser));
    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositive() throws ResponseException {
        facade.register(testUser);
        facade.authToken = null;
        var auth = facade.login(new UserData(testUser.username(), testUser.password(), null));
        assertEquals(auth.username(), testUser.username());
        assertEquals(auth.authToken(), facade.authToken);
        assertNotNull(facade.authToken);
    }

    @Test
    @DisplayName("Login Negative Not Exist")
    public void loginNegative() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.login(testUser));
    }


}
