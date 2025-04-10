package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        String user = Server.userService.authenticate(command.getAuthToken()).username();
        connections.add(session, command.getGameID());

        switch (command.getCommandType()) {
            case CONNECT -> connect(session, user, command);
            case MAKE_MOVE -> makeMove(session, user, command);
            case LEAVE -> leave(session, user, command);
            case RESIGN -> resign(session, user, command);
        }
    }

    private void connect(Session session, String user, UserGameCommand command) throws ResponseException {
        var gameID = command.getGameID();
        connections.add(session, gameID);
        sendGame(session, gameID);
//        Add notification
    }

    private void makeMove(Session session, String user, UserGameCommand command) {
    }

    private void leave(Session session, String user, UserGameCommand command) {
        connections.remove(session);
//        Add notification
    }

    private void resign(Session session, String user, UserGameCommand command) {
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

}
