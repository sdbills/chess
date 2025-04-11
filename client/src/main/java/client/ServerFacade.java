package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.CreateRequest;
import request.JoinRequest;
import response.CreateResponse;
import response.ListResponse;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType.*;

public class ServerFacade {

    private final String serverURL;
    String authToken;
    HttpCommunicator http;
    WebSocketCommunicator ws;
    NotificationHandler notificationHandler;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        http = new HttpCommunicator(serverURL);
    }

    public ServerFacade(String serverURL, NotificationHandler notificationHandler) {
        this.serverURL = serverURL;
        http = new HttpCommunicator(serverURL);
        this.notificationHandler = notificationHandler;
    }

   public AuthData register(UserData req) throws ResponseException {
        var res = http.register(req);
        authToken = res.authToken();
        return res;
   }

   public AuthData login(UserData req) throws ResponseException {
        var res = http.login(req);
        authToken = res.authToken();
        return res;
   }

   public void logout() throws ResponseException {
        http.logout();
        authToken = null;
   }

   public CreateResponse create(CreateRequest req) throws ResponseException {
        return http.create(req);
    }

   public ListResponse listGames() throws ResponseException {
        return http.listGames();
   }

   public void join(JoinRequest req) throws ResponseException {
        http.join(req);
   }

   public void connect(int gameID, ChessGame.TeamColor color, boolean isPlayer) throws ResponseException {
        ws = new WebSocketCommunicator(serverURL, notificationHandler);
        ConnectCommand message;
        if (isPlayer) {
            message = new ConnectCommand(authToken, gameID, color);
        } else {
            message = new ConnectCommand(authToken, gameID);
        }
        ws.send(new Gson().toJson(message));
   }

    public void leave(int gameID) throws ResponseException {
        var message = new UserGameCommand(LEAVE, authToken, gameID);
        ws.send(new Gson().toJson(message));
        ws = null;
    }

    public void makeMove(Integer gameID, ChessMove move) throws ResponseException {
        var message = new MakeMoveCommand(authToken, gameID, move);
        ws.send(new Gson().toJson(message));
    }

    public void resign(Integer gameID) throws ResponseException {
        var message = new UserGameCommand(RESIGN,authToken,gameID);
        ws.send(new Gson().toJson(message));
    }
}
