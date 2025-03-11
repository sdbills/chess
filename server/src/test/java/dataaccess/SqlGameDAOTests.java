package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SqlGameDAOTests {

    static GameDAO gameDAO;
    GameData testGame1 = new GameData(null,null,null,"game1", new ChessGame());
    GameData testGame2 = new GameData(null,null,null,"game2", new ChessGame());

    @BeforeAll
    static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        gameDAO = new SqlGameDAO();
    }

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE game")) {
                statement.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("Create Game good")
    void createGamePositive() throws DataAccessException, SQLException {
        assertDoesNotThrow(() -> gameDAO.createGame(testGame1));
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT gameID,gameName,whiteUsername,blackUsername,game " +
                    "FROM game WHERE gameName=?")) {
                statement.setString(1, testGame1.gameName());
                try (var res = statement.executeQuery()) {
                    res.next();
                    assertInstanceOf(Integer.class,res.getInt("gameID"));
                    assertEquals(res.getString("gameName"), testGame1.gameName());
                    assertEquals(res.getString("whiteUsername"), testGame1.whiteUsername());
                    assertEquals(res.getString("blackUsername"), testGame1.blackUsername());
                    assertEquals(res.getString("game"), new Gson().toJson(testGame1.game()));
                }
            }
        }
    }

    @Test
    @DisplayName("Create Games diff ID")
    void createGamePositiveID() throws DataAccessException {
        var id1 = gameDAO.createGame(testGame1);
        var id2 = gameDAO.createGame(testGame2);
        assertNotEquals(id2,id1);
    }

    @Test
    @DisplayName("Create Game null")
    void createGameNegative() {
        var badGame = new GameData(null,null,null,null,null);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(badGame));
    }

    @Test
    @DisplayName("Get Game by name Good")
    void getGameByNamePositive() throws DataAccessException {
        gameDAO.createGame(testGame1);
        var game = gameDAO.getGame(testGame1.gameName());
        assertInstanceOf(Integer.class, game.gameID());
        assertEquals(game.whiteUsername(), testGame1.whiteUsername());
        assertEquals(game.blackUsername(), testGame1.blackUsername());
        assertEquals(game.gameName(), testGame1.gameName());
        assertEquals(game.game(), testGame1.game());
    }

    @Test
    @DisplayName("Get Game by Name not in db")
    void getGameByNameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(testGame1.gameName()));
    }

    @Test
    @DisplayName("Get Game by ID Good")
    void getGameByIDPositive() throws DataAccessException {
        var id = gameDAO.createGame(testGame1);
        var game = gameDAO.getGame(id);
        assertEquals(game.whiteUsername(), testGame1.whiteUsername());
        assertEquals(game.blackUsername(), testGame1.blackUsername());
        assertEquals(game.gameName(), testGame1.gameName());
        assertEquals(game.game(), testGame1.game());
    }

    @Test
    @DisplayName("Get Game by ID not in db")
    void getGameByIDNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(0));
    }

    @Test
    @DisplayName("Update Game Join Good")
    void updateGamePositive() throws DataAccessException, SQLException {
        var id = gameDAO.createGame(testGame1);
        var newGame = new GameData(id, "white","black", testGame1.gameName(), testGame1.game());
        gameDAO.updateGame(newGame);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT whiteUsername, blackUsername FROM game WHERE gameID=?")) {
                statement.setInt(1, id);
                try (var res = statement.executeQuery()) {
                    res.next();
                    assertEquals(res.getString("whiteUsername"), newGame.whiteUsername());
                    assertEquals(res.getString("blackUsername"), newGame.blackUsername());
                    assertFalse(res.next());
                }
            }
        }
    }

    @Test
    @DisplayName("Update Game not in db")
    void updateGameNegative() throws SQLException, DataAccessException {
        var id = gameDAO.createGame(testGame1);
        //Updates nothing and doesn't throw (existing checks done by service)
        assertDoesNotThrow(() -> gameDAO.updateGame(new GameData(0,"white","black",null,null)));
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT whiteUsername, blackUsername FROM game WHERE gameID=?")) {
                statement.setInt(1, id);
                try (var res = statement.executeQuery()) {
                    res.next();
                    assertEquals(res.getString("whiteUsername"), testGame1.whiteUsername());
                    assertEquals(res.getString("blackUsername"), testGame1.blackUsername());
                }
            }
        }
    }

    @Test
    @DisplayName("List Games Good")
    void listGamesPositive() throws DataAccessException {
        gameDAO.createGame(testGame1);
        gameDAO.createGame(testGame2);
        var games = gameDAO.listGames();
        assertEquals(2,games.size());
    }

    @Test
    @DisplayName("List Games Empty")
    void listGamesNegative() throws DataAccessException {
        var games = gameDAO.listGames();
        assertEquals(0,games.size());
    }

    @Test
    @DisplayName("Clear Auth Good")
    void clearPositive() throws DataAccessException, SQLException {
        gameDAO.createGame(testGame1);
        gameDAO.createGame(testGame2);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM game")) {
                try (var res = statement.executeQuery()) {
                    assertTrue(res.next());
                    assertTrue(res.next());
                }
            }
        }

        gameDAO.clear();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM auth")) {
                try (var res = statement.executeQuery()) {
                    assertFalse(res.next());
                }
            }
        }
    }
}
