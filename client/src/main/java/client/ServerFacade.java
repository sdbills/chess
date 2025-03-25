package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
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
        var res = makeRequest("POST", "/user", req, AuthData.class);
        authToken = res.authToken();
        return res;
   }

   public AuthData login(UserData req) throws ResponseException {
        var res = makeRequest("POST", "/session", req, AuthData.class);
        authToken = res.authToken();
        return res;
   }

   public void logout() throws ResponseException {
        makeRequest("DELETE", "/session", null, null);
        authToken = null;
   }

   public CreateResponse create(CreateRequest req) throws ResponseException {
        return makeRequest("POST", "/game", req, CreateResponse.class);
   }

   public ListResponse listGames() throws ResponseException {
        return makeRequest("GET", "/game", null, ListResponse.class);
   }

   public void join(JoinRequest req) throws ResponseException {
        makeRequest("PUT", "/game", req, null);
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
            failureCaseThrow(http);
            return readBody(http, responseClass);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
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

    private void failureCaseThrow(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!(status/100 == 2)) {
            throw new ResponseException(status, http.getResponseMessage());
//            try (InputStream err = http.getErrorStream()) {
//                if (err != null) {
//                    throw new ResponseException(status, err.);
//                }
//            }
        }
    }
}
