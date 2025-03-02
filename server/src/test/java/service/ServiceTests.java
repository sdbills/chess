package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.JoinRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {

    UserDAO userDAO = new MemoryUserDAO();
    GameDAO gameDAO = new MemoryGameDAO();
    AuthDAO authDAO = new MemoryAuthDAO();
    UserService userService = new UserService(userDAO,authDAO);
    GameService gameService = new GameService(gameDAO,authDAO);
    UserData testUser = new UserData("User","Pass", "Mail");
    GameData testGame = new GameData(null,null,null,"testGame",null);
    AuthData testAuth = new AuthData("auth","User");


    @BeforeEach
    void setup() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Register Good User")
    void registerTestPositive() throws ServiceException, DataAccessException {
        AuthData auth = userService.register(testUser);
        assertEquals(authDAO.getAuth(auth.authToken()),auth);
    }

    @Test
    @DisplayName("Register Taken User")
    void registerTestNegativeTaken() throws ServiceException, DataAccessException {
        userService.register(testUser);
        assertThrows(ServiceException.class, () -> userService.register(testUser));
    }

    @Test
    @DisplayName("Register Incomplete User")
    void registerTestNegativeInvalid() {
        var badUser = new UserData("user","pass",null);
        assertThrows(ServiceException.class, () -> userService.register(badUser));
    }

    @Test
    @DisplayName("Valid login")
    void loginTestPositive() throws ServiceException, DataAccessException {
        userDAO.createUser(testUser);
        var auth = userService.login(testUser);
        assertEquals(authDAO.getAuth(auth.authToken()),auth);
    }

    @Test
    @DisplayName("Invalid login")
    void loginTestNegative() throws DataAccessException {
        userDAO.createUser(testUser);
        var invalidLog = new UserData(testUser.username(), "badPass",null);
        assertThrows(ServiceException.class, () -> userService.login(invalidLog));
    }

    @Test
    @DisplayName("Good logout")
    void logoutTestPositive() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        userService.logout(testAuth.authToken());
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    @DisplayName("Unauthorized logout")
    void logoutTestNegative() throws DataAccessException {
        authDAO.createAuth(testAuth);
        assertThrows(ServiceException.class, () -> userService.logout("invalid token"));
    }

    @Test
    @DisplayName("Good Creation")
    void createTestPositive() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        var res = gameService.createGame(testGame,testAuth.authToken());
        assertNotNull(gameDAO.getGame(res.gameID()));
    }

    @Test
    @DisplayName("Bad Creation Taken")
    void createTestNegativeTaken() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        gameService.createGame(testGame,testAuth.authToken());
        assertThrows(ServiceException.class, () -> gameService.createGame(testGame,testAuth.authToken()));
    }

    @Test
    @DisplayName("Good List Games")
    void listTestPositive() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        gameDAO.createGame(testGame);
        gameDAO.createGame(new GameData(2,null,null,null,null));
        var gameList = gameService.listGames(testAuth.authToken());
        assertEquals(2, gameList.games().size());
    }

    @Test
    @DisplayName("Unauthorized List Games")
    void listTestNegative() {
        assertThrows(ServiceException.class, () -> gameService.createGame(testGame,testAuth.authToken()));
    }

    @Test
    @DisplayName("Good Join Game")
    void joinTestPositive() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        var id = gameDAO.createGame(testGame);
        gameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE,id),testAuth.authToken());
        assertEquals(gameDAO.getGame(id).whiteUsername(), testAuth.username());

        gameService.joinGame(new JoinRequest(ChessGame.TeamColor.BLACK,id),testAuth.authToken());
        assertEquals(gameDAO.getGame(id).blackUsername(), testAuth.username());
    }

    @Test
    @DisplayName("Bad Join Taken")
    void joinTestNegativeTaken() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        var id = gameDAO.createGame(testGame);
        var req = new JoinRequest(ChessGame.TeamColor.WHITE,id);
        gameService.joinGame(req,testAuth.authToken());
        assertThrows(ServiceException.class, () -> gameService.joinGame(req,testAuth.authToken()));
    }

    @Test
    @DisplayName("Bad Join gameID")
    void joinTestNegativeID() throws DataAccessException {
        authDAO.createAuth(testAuth);
        gameDAO.createGame(testGame);
        var req = new JoinRequest(ChessGame.TeamColor.WHITE,123);
        assertThrows(ServiceException.class, () -> gameService.joinGame(req,testAuth.authToken()));
    }

    @Test
    @DisplayName("Good Authorization")
    void authenticateTestPositive() throws ServiceException, DataAccessException {
        authDAO.createAuth(testAuth);
        var auth = userService.authenticate(testAuth.authToken());
        assertEquals(authDAO.getAuth(auth.authToken()),auth);
    }

    @Test
    @DisplayName("Bad Authorization")
    void authenticateTestNegative() {
        assertThrows(ServiceException.class, () -> userService.authenticate("123312"));
    }



    @Test
    @DisplayName("Clear database")
    void clearTestPositive() throws DataAccessException {
        authDAO.createAuth(testAuth);
        gameDAO.createGame(testGame);
        userDAO.createUser(testUser);

        gameService.clear();
        assertNull(gameDAO.getGame(testGame.gameName()));

        userService.clear();
        assertNull(userDAO.getUser(testUser.username()));
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

}
