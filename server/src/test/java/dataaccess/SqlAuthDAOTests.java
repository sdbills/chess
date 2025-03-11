package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlAuthDAOTests {

    static AuthDAO authDAO;
    AuthData testAuth = new AuthData("atok", "user");

    @BeforeAll
    static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        authDAO = new SqlAuthDAO();
    }

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE auth")) {
                statement.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("Create Auth Good")
    void createAuthPositive() throws DataAccessException, SQLException {
        assertDoesNotThrow(() -> authDAO.createAuth(testAuth));
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT authToken,username FROM auth WHERE username=?")) {
                statement.setString(1, testAuth.username());
                try (var res = statement.executeQuery()) {
                    res.next();
                    assertEquals(res.getString("authToken"), testAuth.authToken());
                    assertEquals(res.getString("username"), testAuth.username());
                }
            }
        }
    }

    @Test
    @DisplayName("Create Auth Null")
    void createAuthNegative() {
        AuthData badAuth = new AuthData(null,null);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(badAuth));
    }

    @Test
    @DisplayName("Get Auth Good")
    void getAuthPositive() throws DataAccessException {
        authDAO.createAuth(testAuth);
        var auth = authDAO.getAuth(testAuth.authToken());
        assertEquals(auth.username(), testAuth.username());
        assertEquals(auth.authToken(), testAuth.authToken());
    }

    @Test
    @DisplayName("Get Auth not in db")
    void getAuthNegative() throws DataAccessException {
        assertNull(authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    @DisplayName("Delete Auth Good")
    void deleteAuthPositive() throws DataAccessException, SQLException {
        authDAO.createAuth(testAuth);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username FROM auth WHERE authToken=?")) {
                statement.setString(1, testAuth.authToken());
                try (var res = statement.executeQuery()) {
                    assertTrue(res.next());
                }
            }
        }

        authDAO.deleteAuth(testAuth);
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username FROM auth WHERE authToken=?")) {
                statement.setString(1, testAuth.authToken());
                try (var res = statement.executeQuery()) {
                    assertFalse(res.next());
                }
            }
        }
    }

    @Test
    @DisplayName("Delete Auth not in db")
    void deleteAuthNegative() {
        assertDoesNotThrow(() -> authDAO.deleteAuth(testAuth));
    }

    @Test
    @DisplayName("Clear Auth Good")
    void clearPositive() throws DataAccessException, SQLException {
        authDAO.createAuth(testAuth);
        authDAO.createAuth(new AuthData("token", testAuth.username()));
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username FROM auth WHERE username=?")) {
                statement.setString(1, testAuth.username());
                try (var res = statement.executeQuery()) {
                    assertTrue(res.next());
                    assertTrue(res.next());
                }
            }
        }

        authDAO.clear();
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username FROM auth WHERE username=?")) {
                statement.setString(1, testAuth.username());
                try (var res = statement.executeQuery()) {
                    assertFalse(res.next());
                }
            }
        }
    }



}
