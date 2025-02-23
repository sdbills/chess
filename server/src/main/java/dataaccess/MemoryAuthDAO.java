package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.authToken(),auth);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auths.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        auths.remove(auth.authToken());
    }

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }
}
