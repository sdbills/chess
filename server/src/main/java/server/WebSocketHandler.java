package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String user;
        try {
            user = Server.userService.authenticate(command.getAuthToken()).username();
            connections.add(session, command.getGameID());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, user, new Gson().fromJson(message, ConnectCommand.class));
                case MAKE_MOVE -> makeMove(session, user, new Gson().fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leave(session, user, command);
                case RESIGN -> resign(session, user, command);
            }
        } catch (ResponseException | DataAccessException e) {
            sendError(session, e.getMessage());
        }
    }

    private void connect(Session session, String user, ConnectCommand command) throws ResponseException, DataAccessException {
        var gameID = command.getGameID();
        if (Server.gameService.getGameData(gameID) == null) {
            sendError(session, "not a valid gameID");
            return;
        }
        connections.add(session, gameID);
        sendGame(session, gameID);
        var color = command.getColor();

        String message = user + " has joined the game as";
        if (color == ChessGame.TeamColor.WHITE) {
            message = message + " white player";
        } else if (color == ChessGame.TeamColor.BLACK) {
            message = message + " black player";
        } else {
            message = message + " an observer";
        }

        NotificationMessage notification = new NotificationMessage(message);
        try {
            connections.broadcastOthers(new Gson().toJson(notification), gameID, session);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void makeMove(Session session, String user, MakeMoveCommand command) throws DataAccessException, ResponseException {
        var gameID = command.getGameID();
        var gameData = Server.gameService.getGameData(gameID);

        if (!Objects.equals(user, gameData.whiteUsername()) && !Objects.equals(user, gameData.blackUsername())) {
            sendError(session, "you are not a player");
            return;
        } else if (gameData.game().isOver()) {
            sendError(session, "the game is over already");
            return;
        }

        var turn = gameData.game().getTeamTurn();
        String userTurn;
        if (turn == ChessGame.TeamColor.WHITE) {
            userTurn = gameData.whiteUsername();
        } else {
            userTurn = gameData.blackUsername();
        }

        if (user.equals(userTurn)) {
            try {
                gameData.game().makeMove(command.getMove());
                Server.gameService.updateGame(gameID, gameData.game());

                var gameMessage = new LoadGameMessage(gameData.game());
                connections.broadcastAll(new Gson().toJson(gameMessage), gameID);

                var moveMessage = user + " moved " + moveNotation(command.getMove());
                NotificationMessage notification = new NotificationMessage(moveMessage);
                connections.broadcastOthers(new Gson().toJson(notification), gameID, session);

                var newTurn = gameData.game().getTeamTurn();
                String message = "";
                if (gameData.game().isInCheck(newTurn)) {
                    message = newTurn.toString() + " is in check";
                } else if (gameData.game().isInCheckmate(newTurn)) {
                    message = newTurn.toString() + " is in checkmate, game over";
                } else if (gameData.game().isInStalemate(newTurn)) {
                    message = "it's a stalemate, game over";
                }
                if (!message.isEmpty()) {
                    notification = new NotificationMessage(message);
                    connections.broadcastAll(new Gson().toJson(notification), gameID);
                }

            } catch (InvalidMoveException e) {
                sendError(session, "not a valid move");
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else {
            sendError(session, "it is not your turn");
        }
    }

    private String moveNotation(ChessMove move) {
        var letter = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        var start = move.getStartPosition();
        var end = move.getEndPosition();
        String note = letter[start.getColumn()-1] + start.getRow() +
                letter[end.getColumn()-1] + end.getRow();

        var promo = move.getPromotionPiece();
        return switch (promo) {
                case QUEEN -> note +" Q";
                case BISHOP -> note + " B";
                case KNIGHT -> note + " N";
                case ROOK -> note + " R";
                case null, default -> note;
        };
    }

    private void leave(Session session, String user, UserGameCommand command) throws ResponseException {
        var gameID = command.getGameID();
        connections.remove(session);
        String message = user + " has left the game";
        NotificationMessage notification = new NotificationMessage(message);
        try {
            connections.broadcastOthers(new Gson().toJson(notification), gameID, session);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void resign(Session session, String user, UserGameCommand command) throws DataAccessException, ResponseException {
        var gameID = command.getGameID();
        var gameData = Server.gameService.getGameData(gameID);
        if (!Objects.equals(user, gameData.whiteUsername()) || Objects.equals(user, gameData.blackUsername())) {
            sendError(session, "you are not a player, can't resign");
            return;
        } else if (gameData.game().isOver()) {
            sendError(session, "the game is over already");
            return;
        }

        gameData.game().endGame();
        Server.gameService.updateGame(gameID, gameData.game());

        String message = user + " has resigned";
        var notification = new NotificationMessage(message);
        try {
            connections.broadcastAll(new Gson().toJson(notification), gameID);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void sendGame(Session session, Integer gameID) throws ResponseException {
        try {
            var game = Server.gameService.getGame(gameID);
            var gameMessage = new LoadGameMessage(game);
            session.getRemote().sendString(new Gson().toJson(gameMessage));
        } catch (IOException | DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void sendError(Session session, String message) throws ResponseException {
        try {
            var errorMessage = new ErrorMessage(message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
