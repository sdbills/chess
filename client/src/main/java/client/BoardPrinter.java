package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    ChessGame.TeamColor color;
    ChessGame game;
    ChessPosition start;
    ArrayList<ChessPosition> end;

    public BoardPrinter (ChessGame game, ChessGame.TeamColor color) {
        this.color = color;
        this.game = game;
    }

    public BoardPrinter (ChessGame game, ChessGame.TeamColor color, ChessPosition start) {
        this.color = color;
        this.game = game;
        this.start = start;
        Collection<ChessMove> moves = game.validMoves(start);
        if (moves != null) {
            this.end = new ArrayList<>();
            for (ChessMove m : moves) {
                end.add(m.getEndPosition());
            }
        }
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
        if (start != null) {
            var pos = new ChessPosition(row, col);
            if (pos.equals(start)) {
                return SET_BG_COLOR_YELLOW;
            } else if (end != null && !end.isEmpty() && end.contains(pos)) {
                if (Math.ceilMod(tot,2) == 0) {
                    return SET_BG_COLOR_DARK_GREEN;
                } else {
                    return SET_BG_COLOR_GREEN;
                }
            }
        }

        if (Math.ceilMod(tot,2) == 0) {
            return SET_BG_COLOR_RED;
        } else {
            return SET_BG_COLOR_MAGENTA;
        }
    }
}
