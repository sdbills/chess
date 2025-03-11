package dataaccess;

import model.UserData;

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

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
    }

}
