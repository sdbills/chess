package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.CreateRequest;
import request.JoinRequest;
import response.CreateResponse;
import response.ListResponse;

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

   public void connect() throws ResponseException {
        ws = new WebSocketCommunicator(serverURL, notificationHandler);
   }


}
