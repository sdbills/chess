package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import exception.ResponseException;

import java.util.UUID;

public class UserService extends Service {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        super(authDAO);
        this.userDAO = userDAO;
    }

    public AuthData register(UserData req) throws DataAccessException, ResponseException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new ResponseException(400, "bad request");
        }
        var user = userDAO.getUser(req.username());
        if (user == null) {
            userDAO.createUser(new UserData(req.username(), req.password(), req.email()));
        } else {
            throw new ResponseException(403, "already taken");
        }
        return createAuth(req.username());
    }

    public AuthData login(UserData req) throws DataAccessException, ResponseException {
        var user = userDAO.getUser(req.username());
        if (user != null && BCrypt.checkpw(req.password(),user.password())) {
            return createAuth(req.username());
        } else {
            throw new ResponseException(401, "unauthorized");
        }
    }

    public void logout(String authToken) throws DataAccessException, ResponseException {
        var auth = authenticate(authToken);
        authDAO.deleteAuth(auth);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        super.clear();
    }

    private AuthData createAuth(String username) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authDAO.createAuth(auth);
        return auth;
    }
}
