package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService extends Service{
    private final UserDAO userDao;

    public UserService(UserDAO userDao, AuthDAO authDAO) {
        super(authDAO);
        this.userDao = userDao;
    }

    public AuthData register(UserData req) throws DataAccessException {
        var user = userDao.getUser(req.username());
        if (user == null) {
            userDao.createUser(new UserData(req.username(), req.password(), req.email()));
        }
        return login(req);
    }

    public AuthData login(UserData req) throws DataAccessException {

        return createAuth(req.username());
    }

    private AuthData createAuth(String username) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken,username);
        authDAO.createAuth(auth);
        return auth;
    }

}
