package dataaccess;

import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SqlAuthDAO extends SqlDAO implements AuthDAO {

    public SqlAuthDAO() throws DataAccessException {
        String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS auth (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`)
            )"""
        };
        configureDatabase(createStatements);
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth (authToken, username) VALUES (?, ?)")) {
                statement.setString(1, auth.authToken());
                statement.setString(2, auth.username());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Invalid authentication: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

}
