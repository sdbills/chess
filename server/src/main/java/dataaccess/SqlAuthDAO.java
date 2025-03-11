package dataaccess;

import model.AuthData;

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
        try (var conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT username FROM auth where authToken=?")) {
                statement.setString(1, authToken);
                try (var res = statement.executeQuery()) {
                    if (res.next()) {
                        return new AuthData(authToken,res.getString("username"));
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Auth not found: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

}
