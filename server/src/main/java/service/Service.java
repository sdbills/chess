package service;

import dataaccess.*;

public class Service {
    protected final AuthDAO authDAO;

    public Service(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }
}
