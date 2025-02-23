package service;

import dataaccess.*;

public class Service {
    protected AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }
}
