package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SqlUserDAO extends SqlDAO implements UserDAO {

    public SqlUserDAO() throws DataAccessException {
        String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS user (
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            PRIMARY KEY (`username`)
            )"""
        };
        configureDatabase(createStatements);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")) {
                String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

                statement.setString(1, user.username());
                statement.setString(2, hashedPassword);
                statement.setString(3, user.email());
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            throw new DataAccessException("Invalid user creation: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username, password, email FROM user where username=?")) {
                statement.setString(1, username);
                try (var res = statement.executeQuery()) {
                    if (!res.next()) {
                        return null;
                    }
                    res.next();
                    return new UserData(username,res.getString("password"),res.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
    }

}
