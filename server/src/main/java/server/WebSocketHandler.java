package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        String user = Server.authenticate(command.getAuthToken()).username();
        connections.add(session, command.getGameID());

        switch (command.getCommandType()) {
            case CONNECT -> connect(session, user, command);
            case MAKE_MOVE -> makeMove(session, user, command);
            case LEAVE -> leaveGame(session, user, command);
            case RESIGN -> resign(session, user, command);
        }
    }

    private void connect(Session session, String user, UserGameCommand command) {
    }

    private void makeMove(Session session, String user, UserGameCommand command) {
    }

    private void leaveGame(Session session, String user, UserGameCommand command) {
    }

    private void resign(Session session, String user, UserGameCommand command) {
    }

}
