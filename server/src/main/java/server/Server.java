package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import request.JoinRequest;
import service.GameService;
import service.ServiceException;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Spark;

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
        Spark.delete("/session", this::logoutHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.put("/game", this::joinGameHandler);
        Spark.exception(ServiceException.class, this::responseExceptionHandler);
        Spark.exception(DataAccessException.class, this::dataExceptionHandler);

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
        return "{}";
    }

    private Object registerHandler(Request req, Response res) throws DataAccessException, ServiceException {
        var userReq = new Gson().fromJson(req.body(), UserData.class);
        AuthData result = userService.register(userReq);
        return new Gson().toJson(result);
    }

    private Object loginHandler(Request req, Response res) throws DataAccessException, ServiceException {
        var userReq = new Gson().fromJson(req.body(), UserData.class);
        AuthData result = userService.login(userReq);
        return new Gson().toJson(result);
    }

    private Object logoutHandler(Request req, Response res) throws DataAccessException, ServiceException {
        String authToken = req.headers("authorization");
        userService.logout(authToken);
        return "{}";
    }

    private Object createGameHandler(Request req, Response res) throws DataAccessException, ServiceException {
        String authToken = req.headers("authorization");
        var gameReq = new Gson().fromJson(req.body(), GameData.class);
        var gameRes = gameService.createGame(gameReq, authToken);
        return new Gson().toJson(gameRes);
    }

    private Object listGamesHandler(Request req, Response res) throws DataAccessException, ServiceException {
        String authToken = req.headers("authorization");
        var games = gameService.listGames(authToken);
        return new Gson().toJson(games);
    }

    private Object joinGameHandler(Request req, Response res) throws DataAccessException, ServiceException {
        String authToken = req.headers("authorization");
        var joinReq = new Gson().fromJson(req.body(), JoinRequest.class);
        gameService.joinGame(joinReq, authToken);
        return "{}";
    }

    private void responseExceptionHandler(ServiceException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body(ex.toJson());
    }

    private void dataExceptionHandler(DataAccessException ex, Request req, Response res) {
        res.status(500);
        res.body(ex.toJson());
    }
}
