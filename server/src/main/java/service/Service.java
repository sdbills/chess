package service;

import dataaccess.*;
import model.AuthData;

public class Service {
    protected AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }

    public AuthData authenticate(String authToken) throws DataAccessException, ResponseException {

        var auth = authDAO.getAuth(authToken);
        if (auth != null) {
            return auth;
        } else {
            throw new ResponseException(401, "unauthorized");
        }
    }
}
