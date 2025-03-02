package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class Service {
    protected AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }

    public AuthData authenticate(String authToken) throws DataAccessException, ServiceException {
        var auth = authDAO.getAuth(authToken);
        if (auth != null) {
            return auth;
        } else {
            throw new ServiceException(401, "unauthorized");
        }
    }
}
