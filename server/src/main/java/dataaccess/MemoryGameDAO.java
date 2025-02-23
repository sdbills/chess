package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int lastID = 0;

    @Override
    public int createGame(GameData game) throws DataAccessException {
        lastID++;
        var newGame = new GameData(lastID, game.whiteUsername(), game.blackUsername(),
                            game.gameName(),game.game());
        games.put(lastID,newGame);
        return lastID;
    }

    @Override
    public GameData getGame(String gameName) throws DataAccessException {
        for (int gameID : games.keySet()) {
            if (games.get(gameID).gameName().equals(gameName)) {
                return games.get(gameID);
            }
        }
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void deleteGame(GameData game) throws DataAccessException {
        games.remove(game.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
