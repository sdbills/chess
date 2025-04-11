package client;

import chess.ChessGame;
import chess.ChessPosition;
import exception.ResponseException;
import ui.Repl;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class GameClient implements Client {
    private final ServerFacade server;
    private final int gameID;
    private final ChessGame.TeamColor color;
    private final Repl repl;
    private ChessGame game;
    private final boolean isPlayer;

    public GameClient(ServerFacade server, int gameID, ChessGame.TeamColor color, Repl repl, boolean isPlayer) throws ResponseException {
        this.server = server;
        this.gameID = gameID;
        this.color = color;
        this.repl = repl;
        this.isPlayer = isPlayer;
        server.connect(gameID);
//        System.out.println(printBoard());
    }

    @Override
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().strip().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "";
            var params = Arrays.copyOfRange(tokens,1,tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> "invalid command, enter 'help' for valid command";
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    private String help() {
        return """
                redraw - to display the chess board again
                leave - to leave the game
                move <start> <end> <promotion?Q|B|N|R> - to move a piece from the start to end positions
                resign - to forfeit the game
                highlight <position> - to highlight the legal moves of a piece
                help - to get helpful information""";
    }

    public String redraw() {
        return new BoardPrinter(game, color).printBoard();
    }

    private String leave() throws ResponseException {
        server.leave(gameID);
        repl.setPost();
        return "LEFT THE GAME";
    }

    private String makeMove(String[] params) {
        return "MOVED";
    }

    private String resign() {
        return "RESIGNED";
    }

    private String highlight(String[] params) {
        return "HIGHLIGHTED";
    }
}
