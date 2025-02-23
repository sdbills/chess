package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException;

    GameData getGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void deleteGame(GameData game) throws DataAccessException;

    void clear() throws DataAccessException;
}
