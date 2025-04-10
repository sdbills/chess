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
    private final ChessGame game = new ChessGame();
    private final boolean isPlayer;

    public GameClient(ServerFacade server, int gameID, ChessGame.TeamColor color, Repl repl, boolean isPlayer) throws ResponseException {
        this.server = server;
        this.gameID = gameID;
        this.color = color;
        this.repl = repl;
        this.isPlayer = isPlayer;
        server.connect(gameID);
        System.out.println(printBoard());
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

    private String help() {
        return """
                redraw - to display the chess board again
                leave - to leave the game
                move <start> <end> - to move a piece from the start to end positions
                resign - to forfeit the game
                highlight <position> - to highlight the legal moves of a piece
                help - to get helpful information""";
    }

    private String redraw() {
        return printBoard();
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

    public String printBoard() {
        StringBuilder out = new StringBuilder();

        boolean reverse = color.equals(ChessGame.TeamColor.BLACK);
        out.append(SET_TEXT_BOLD);
        out.append(letterRow(reverse));
        out.append("\n");

        for (int r = 1; r < 9; r++) {
            int row = reverse ? r : 9-r;
            out.append(rowString(row, reverse));
            out.append("\n");
        }

        out.append(letterRow(reverse));
        out.append(RESET_TEXT_BOLD_FAINT);
        return out.toString();
    }

    private String letterRow(boolean reverse) {
        StringBuilder out = new StringBuilder();
        String letters = "    a  b  c  d  e  f  g  h    ";
        if (reverse) {
            letters = new StringBuilder(letters).reverse().toString();
        }

        setBorderColor(out);
        out.append(letters);
        resetColors(out);

        return out.toString();
    }

    private String rowString(int row, boolean reverse) {
        StringBuilder out = new StringBuilder();
        setBorderColor(out);
        out.append(" ").append(row).append(" ");
        for (int c = 1; c < 9; c++) {
            int col = reverse ? 9-c : c;
            var pos = new ChessPosition(row, col);
            var piece = game.getBoard().getPiece(pos);

            out.append(tileColor(row,col));
            if (piece != null) {
                switch (piece.getTeamColor()) {
                    case WHITE -> out.append(SET_TEXT_COLOR_WHITE);
                    case BLACK -> out.append(SET_TEXT_COLOR_BLACK);
                }
                switch (piece.getPieceType()) {
                    case QUEEN -> out.append(" Q ");
                    case KING -> out.append(" K ");
                    case PAWN -> out.append(" P ");
                    case ROOK -> out.append(" R ");
                    case KNIGHT -> out.append(" N ");
                    case BISHOP -> out.append(" B ");
                }
            } else {
                out.append("   ");
            }
        }
        setBorderColor(out);
        out.append(" ").append(row).append(" ");
        resetColors(out);
        return out.toString();
    }

    private void resetColors(StringBuilder out) {
        out.append(RESET_BG_COLOR);
        out.append(RESET_TEXT_COLOR);
    }

    private void setBorderColor(StringBuilder out) {
        out.append(SET_BG_COLOR_LIGHT_GREY);
        out.append(SET_TEXT_COLOR_BLACK);
    }

    private String tileColor(int row, int col) {
        int tot = row+col;
        if (Math.ceilMod(tot,2) == 0) {
            return SET_BG_COLOR_RED;
        } else {
            return SET_BG_COLOR_MAGENTA;
        }
    }


}
