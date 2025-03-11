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

}
