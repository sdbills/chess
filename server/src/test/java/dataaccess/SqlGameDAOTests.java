package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
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
    void createGameNegative() throws DataAccessException {
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


}
