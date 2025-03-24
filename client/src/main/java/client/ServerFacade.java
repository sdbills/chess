package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.CreateRequest;
import request.JoinRequest;
import response.CreateResponse;
import response.ListResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverURL;
    String authToken;

    public ServerFacade(int port) {
        serverURL = "http://localhost:"+port;
    }

   public AuthData register(UserData req) throws ResponseException {
        var path = "/user";
        var res = makeRequest("POST", path, req, AuthData.class);
        authToken = res.authToken();
        return res;
   }

   public AuthData login(UserData req) throws ResponseException {
        var path = "/session";
        var res = makeRequest("POST", path, req, AuthData.class);
        authToken = res.authToken();
        return res;
   }

   public void logout() throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, null);
        authToken = null;
   }

   public CreateResponse create(CreateRequest req) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, req, CreateResponse.class);
   }

   public ListResponse listGames() throws ResponseException {
        var path = "/game";
        return makeRequest("GET", path, null, ListResponse.class);
   }

   public void join(JoinRequest req) throws ResponseException {
        var path = "/game";
        makeRequest("PUT", path, req, null);
   }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }

            writeRequest(request, http);
            http.connect();
//            var status = http.getResponseCode();
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void writeRequest(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String req = new Gson().toJson(request);
            try (OutputStream body = http.getOutputStream()) {
                body.write(req.getBytes());
            }
        }
    }
}
