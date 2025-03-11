package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(),hashedPassword,user.email()));
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
