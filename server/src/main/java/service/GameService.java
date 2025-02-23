package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class GameService extends Service {
    GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
