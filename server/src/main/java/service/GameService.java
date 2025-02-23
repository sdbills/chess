package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.ResponseException;
import model.GameData;
import request.joinRequest;

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

    public int createGame(GameData req, String authToken) throws DataAccessException, ResponseException {
        authenticate(authToken);

        if (gameDAO.getGame(req.gameName()) == null) {
            var game = new GameData(null, null,
                    null, req.gameName(), new ChessGame());
            return gameDAO.createGame(game);
        } else {
            throw new DataAccessException("Already Taken");
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, ResponseException {
        authenticate(authToken);

        var games = new java.util.ArrayList<>(gameDAO.listGames());
        games.replaceAll(game -> new GameData(game.gameID(),
                game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        return games;
    }

    public void joinGame(joinRequest req, String authToken) throws DataAccessException, ResponseException {
        var auth = authenticate(authToken);
        var game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new DataAccessException("Bad Request");
        }

        var whiteUser = game.whiteUsername();
        var blackUser = game.blackUsername();

        if (req.playerColor().equals("WHITE")) {
            if (game.whiteUsername() == null) {
                whiteUser = auth.username();
            } else {
                throw new DataAccessException("Already Taken");
            }
        } else if (req.playerColor().equals("BLACK")) {
            if (game.blackUsername() == null) {
                blackUser = auth.username();
            } else {
                throw new DataAccessException("Already Taken");
            }
        } else {
            throw new DataAccessException("Bad Request");
        }
        gameDAO.updateGame(new GameData(game.gameID(), whiteUser,
                blackUser, game.gameName(), game.game()));
    }
}
