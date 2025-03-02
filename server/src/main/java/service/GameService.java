package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import request.JoinRequest;
import response.CreateResponse;
import response.ListResponse;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class GameService extends Service {
    GameDAO gameDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        super(authDAO);
        this.gameDAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public CreateResponse createGame(GameData req, String authToken) throws DataAccessException, ServiceException {
        authenticate(authToken);

        if (gameDAO.getGame(req.gameName()) == null) {
            var game = new GameData(null, null,
                    null, req.gameName(), new ChessGame());
            var gameID = gameDAO.createGame(game);
            return new CreateResponse(gameID);
        } else {
            throw new ServiceException(400, "bad request");
        }
    }

    public ListResponse listGames(String authToken) throws DataAccessException, ServiceException {
        authenticate(authToken);

        var games = new java.util.ArrayList<>(gameDAO.listGames());
        games.replaceAll(game -> new GameData(game.gameID(),
                game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        return new ListResponse(games);
    }

    public void joinGame(JoinRequest req, String authToken) throws DataAccessException, ServiceException {
        var auth = authenticate(authToken);
        if (req.gameID() == null) {
            throw new ServiceException(400, "bad request");
        }
        var game = gameDAO.getGame(req.gameID());
        if (game == null) {
            throw new ServiceException(400, "bad request");
        }

        var whiteUser = game.whiteUsername();
        var blackUser = game.blackUsername();

        if (req.playerColor() == null) {
            throw new ServiceException(400, "bad request");
        } else if (req.playerColor().equals(WHITE)) {
            if (game.whiteUsername() == null) {
                whiteUser = auth.username();
            } else {
                throw new ServiceException(403, "already taken");
            }
        } else if (req.playerColor().equals(BLACK)) {
            if (game.blackUsername() == null) {
                blackUser = auth.username();
            } else {
                throw new ServiceException(403, "already taken");
            }
        }
        gameDAO.updateGame(new GameData(game.gameID(), whiteUser,
                blackUser, game.gameName(), game.game()));
    }
}
