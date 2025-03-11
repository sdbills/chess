package dataaccess;

import model.AuthData;

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
