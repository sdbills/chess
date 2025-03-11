package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    static UserDAO userDAO;
    UserData testUser = new UserData("User","Pass", "Mail");

    @BeforeAll
    static void createDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        userDAO = new SqlUserDAO();
    }

    @BeforeEach
    void setUp() throws SQLException, DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE user")) {
                statement.executeUpdate();
            }
        }
    }

    @Test
    @DisplayName("Create User Good")
    void createUserPositive() throws DataAccessException, SQLException {
        assertDoesNotThrow(() -> userDAO.createUser(testUser));
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user WHERE username=?")) {
                statement.setString(1, testUser.username());
                var res = statement.executeQuery();
                res.next();
                assertEquals(res.getString("username"),testUser.username());
                assertTrue(BCrypt.checkpw(testUser.password(), res.getString("password")));
                assertEquals(res.getString("email"),testUser.email());
            }
        }
    }

    @Test
    @DisplayName("Create User Bad")
    void createUserNegative() {
        UserData badUser = new UserData(null,null,null);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(badUser));
    }


}
