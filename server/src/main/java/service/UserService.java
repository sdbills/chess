package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService extends Service{
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
    }

    public AuthData register(UserData req) throws DataAccessException {
        var user = userDAO.getUser(req.username());
        if (user == null) {
            userDAO.createUser(new UserData(req.username(), req.password(), req.email()));
        }
        return createAuth(req.username());
    }

    public AuthData login(UserData req) throws DataAccessException {

        return createAuth(req.username());
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        super.clear();
    }

    private AuthData createAuth(String username) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken,username);
        authDAO.createAuth(auth);
        return auth;
    }

}
