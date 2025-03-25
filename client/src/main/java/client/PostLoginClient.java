package client;

import exception.ResponseException;
import request.CreateRequest;

import java.util.Arrays;

public class PostLoginClient implements Client{
    private final ServerFacade server;

    public PostLoginClient(ServerFacade server) {
        this.server = server;
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().strip().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "";
            var params = Arrays.copyOfRange(tokens,1,tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> "invalid command, enter 'help' for valid command";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return """
                create <NAME> - to create a new game
                list - to find games
                join <ID> <WHITE|BLACK> - to join a game as a color
                observe <ID> - to view a game
                logout - to sign out
                help - to get helpful information""";
    }

    private String create(String[] params) throws ResponseException {
        if (params.length == 1) {
            server.create(new CreateRequest(params[0]));
            return "CREATED";
        }
        return "invalid number of parameters, enter 'help' for valid command";
    }

    private String list() {
        return "LISTED";
    }

    private String join(String[] params) {
        return "JOINED";
    }

    private String observe(String[] params) {
        return "OBSERVED";
    }

    private String logout() throws ResponseException {
       server.logout();
       return "LOGGED OUT";
    }
}
