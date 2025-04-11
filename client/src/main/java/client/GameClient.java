package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import ui.Repl;

import java.util.Arrays;

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
        server.connect(gameID, color, isPlayer);
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
                move <start> <end> <promotion?Q|B|N|R> - to move a piece from the start to end positions (a1 format)
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
        if (!isPlayer) {
            return "not a player, can't make a move";
        } else if (game.isOver()) {
            return "the game is already over";
        }
        if (params.length == 2 || params.length == 3) {
            try {
                var start = parsePosition(params[0]);
                var end = parsePosition(params[1]);
                ChessPiece.PieceType promo = getPromotionPiece(params);

                ChessMove move = new ChessMove(start, end, promo);
                var validMoves = game.validMoves(start);
                if (validMoves != null && validMoves.contains(move)) {
                    server.makeMove(gameID, move);
                    return "moved " + params[0]+params[1];
                } else {
                    return "not a valid move, make sure to include promotion if necessary and not otherwise";
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "invalid number of parameters, enter 'help' for valid parameters";
        }
    }

    private ChessPiece.PieceType getPromotionPiece(String[] params) throws Exception {
        ChessPiece.PieceType promo = null;
        if (params.length == 3) {
            switch (params[2]) {
                case "n" -> promo = ChessPiece.PieceType.KNIGHT;
                case "q" -> promo = ChessPiece.PieceType.QUEEN;
                case "b" -> promo = ChessPiece.PieceType.BISHOP;
                case "r" -> promo = ChessPiece.PieceType.ROOK;
                default -> throw new Exception("not a valid promotion piece");
            }
        }
        return promo;
    }

    private ChessPosition parsePosition(String pos) throws Exception {
        if (pos.length() == 2) {
            var a = pos.charAt(0);
            var b = Character.getNumericValue(pos.charAt(1));
            switch (a) {
                case 'a' -> a=1;
                case 'b' -> a=2;
                case 'c' -> a=3;
                case 'd' -> a=4;
                case 'e' -> a=5;
                case 'f' -> a=6;
                case 'g' -> a=7;
                case 'h' -> a=8;
                default -> throw new Exception("must be in a-h");
            }
            if (b < 9 && b > 0) {
                return new ChessPosition(b, a);
            } else {
                throw new Exception("must be in 1-8");
            }
        } else {
            throw new Exception("position must match format 'a1'");
        }
    }

    private String resign() throws ResponseException {
        if (!isPlayer) {
            return "not a player, can't make a move";
        }
        server.resign(gameID);
        return "RESIGNED";
    }

    private String highlight(String[] params) {
        if (params.length == 1) {
            try {
                var position = parsePosition(params[0]);
                return new BoardPrinter(game, color, position).printBoard();
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "invalid number of parameters, enter 'help' for valid parameters";
        }
    }
}
