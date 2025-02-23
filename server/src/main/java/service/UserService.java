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
        } else {
            throw new DataAccessException("Bad Request");
        }
        return createAuth(req.username());
    }

    public AuthData login(UserData req) throws DataAccessException {
        var user = userDAO.getUser(req.username());
        if (user.password().equals(req.password()))
            return createAuth(req.username());
        else {
            throw new DataAccessException("Bad Request");
        }
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        super.clear();
    }

    public void logout(String authToken) throws DataAccessException {
        var auth = authenticate(authToken);
        authDAO.deleteAuth(auth);
    }

    private AuthData createAuth(String username) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken,username);
        authDAO.createAuth(auth);
        return auth;
    }

}
