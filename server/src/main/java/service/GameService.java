package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import java.util.Collection;

public class GameService extends Service {
    GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public int createGame(GameData req, String authToken) throws DataAccessException {
        authenticate(authToken);

        if (gameDAO.getGame(req.gameName()) == null) {
            var game = new GameData(null, null,
                    null, req.gameName(), new ChessGame());
            return gameDAO.createGame(game);
        } else {
            throw new DataAccessException("Already Taken");
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        authenticate(authToken);

        var games = new java.util.ArrayList<>(gameDAO.listGames());
        games.replaceAll(game -> new GameData(game.gameID(),
                game.whiteUsername(), game.whiteUsername(), game.gameName(), null));
        return games;
    }
}
