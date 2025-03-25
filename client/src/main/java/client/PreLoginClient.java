package client;

import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

public class PreLoginClient implements Client {
    private final ServerFacade server;

    public PreLoginClient(ServerFacade server) {
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
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> "invalid command, enter 'help' for valid command";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to sign in
                quit - to exit the program
                help - to get helpful information""";
    }

    private String login(String[] params) throws ResponseException {
        if (params.length == 2) {
            UserData userRequest = new UserData(params[0], params[1], null);
            server.login(userRequest);
            return "LOGGED IN";
        }
        return "invalid number of parameters, enter 'help' for valid parameters";
    }

    private String register(String[] params) throws ResponseException {
        if (params.length == 3) {
            UserData userRequest = new UserData(params[0], params[1], params[2]);
            server.register(userRequest);
            return "REGISTERED";
        }
        return "invalid number of parameters, enter 'help' for valid parameters";
    }
}
