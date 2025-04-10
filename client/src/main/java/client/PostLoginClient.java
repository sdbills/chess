package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import request.CreateRequest;
import request.JoinRequest;
import ui.Repl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class PostLoginClient implements Client{
    private final ServerFacade server;
    private final Repl repl;
    ArrayList<GameData> games;

    public PostLoginClient(ServerFacade server, Repl repl) {
        this.server = server;
        this.repl = repl;
        try {
            updateGames();
        } catch (ResponseException e) {
            System.out.println("FAILED INITIALIZATION, RESTART");
        }
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
            return "CREATED " + params[0];
        }
        return "invalid number of parameters, enter 'help' for valid parameters";
    }

    private String list() throws ResponseException {
        updateGames();
        return gamesString();
    }

    private void updateGames() throws ResponseException {
        games = server.listGames().games();
    }

    private String gamesString() {
        if (games.isEmpty()) {
            return "No Games Created Yet";
        }
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < games.size(); i++) {
            out.append(i+1);
            out.append(". Game Name: ");

            var game = games.get(i);
            out.append(game.gameName());

            var whiteUser = game.whiteUsername() == null ? "none" : game.whiteUsername();
            var blackUser = game.blackUsername() == null ? "none" : game.blackUsername();
            out.append("    White: ").append(whiteUser).append("   Black: ").append(blackUser);
            out.append("\n");
        }
        return out.toString();
    }

    private String join(String[] params) throws ResponseException {
        if (params.length == 2) {
            ChessGame.TeamColor color;
            if (Objects.equals(params[1], "white") || Objects.equals(params[1], "w")) {
                color = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(params[1], "black") || Objects.equals(params[1], "b")) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                return "invalid color parameter, enter 'help' for valid parameters";
            }

            int id;
            try {
                id = games.get(Integer.parseInt(params[0])-1).gameID();
            } catch (NumberFormatException e) {
                return "invalid id parameter, must be a number";
            } catch (IndexOutOfBoundsException e) {
                return "invalid id parameter, must be number corresponding to games list";
            }

            server.join(new JoinRequest(color, id));
            try {
                repl.setGame(id, color, true);
            } catch (ResponseException e) {
                return "failed to connect to server";
            }
            return "JOINED AS " + color;
        }
        return "invalid number of parameters, enter 'help' for valid parameters";
    }

    private String observe(String[] params) {
        if (params.length == 1) {
            int id;
            try {
                id = games.get(Integer.parseInt(params[0])-1).gameID();
            } catch (NumberFormatException e) {
                return "invalid id parameter, must be a number";
            } catch (IndexOutOfBoundsException e) {
                return "invalid id parameter, must be number corresponding to games list";
            }
            try {
                repl.setGame(id, ChessGame.TeamColor.WHITE, false);
            } catch (ResponseException e) {
                return "failed to connect to server";
            }
            return "OBSERVING GAME " + id;
        }
        return "invalid number of parameters, enter 'help' for valid parameters";
    }

    private String logout() throws ResponseException {
       server.logout();
       repl.setPre();
       return "LOGGED OUT";
    }
}
