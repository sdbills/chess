package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import request.CreateRequest;
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
    public void logoutNegative() {
        assertThrows(ResponseException.class, () -> facade.login(testUser));
    }

    @Test
    @DisplayName("Logout Positive")
    public void logoutPositive() throws ResponseException {
        facade.register(testUser);
        facade.logout();
        assertNull(facade.authToken);
    }

    @Test
    @DisplayName("Logout Negative")
    public void loginNegative() {
        assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    @DisplayName("Create Positive")
    public void createGamePositive() throws ResponseException {
        facade.register(testUser);
        var res = facade.create(new CreateRequest("gName"));
        assertTrue(res.gameID() > 0);
    }

    @Test
    @DisplayName("Create Negative Invalid Names")
    public void createGameNegative() throws ResponseException {
        facade.register(testUser);
        assertThrows(ResponseException.class, () -> facade.create(new CreateRequest(null)));
        facade.create(new CreateRequest("gName"));
        assertThrows(ResponseException.class, () -> facade.create(new CreateRequest("gName")));
    }

    @Test
    @DisplayName("List Games Positive")
    public void listGamesPositive() throws ResponseException {
        facade.register(testUser);
        facade.create(new CreateRequest("game1"));
        facade.create(new CreateRequest("game2"));
        var games = facade.listGames();
        assertEquals(2, games.games().size());
    }

    @Test
    @DisplayName("List Games Negative Unauthorized")
    public void listGamesNegative() {
        assertThrows(ResponseException.class, () -> facade.listGames());
    }
}
