package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO{

    public SqlUserDAO() throws DataAccessException {
        configureUserTable();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            `id` int NOT NULL AUTO_INCREMENT,
            `username` varchar(256) NOT NULL,
            `password` varchar(256) NOT NULL,
            `email` varchar(256) NOT NULL,
            PRIMARY KEY (`id`)
            )
            """
    };

    private void configureUserTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
