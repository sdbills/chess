package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
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
    }
}
