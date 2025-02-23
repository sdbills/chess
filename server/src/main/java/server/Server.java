package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::registerHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/sessino", this::logoutHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }


    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        userService.clear();
        gameService.clear();
        return "";
    }

    private Object registerHandler(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        AuthData result = userService.register(user);
        return new Gson().toJson(result);
    }

    private Object loginHandler(Request req, Response res) throws DataAccessException {
        var user = new Gson().fromJson(req.body(),UserData.class);
        AuthData result = userService.login(user);
        return new Gson().toJson(result);
    }

    private Object logoutHandler(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("authorization");
        userService.logout(authToken);
        return "";
    }
}
