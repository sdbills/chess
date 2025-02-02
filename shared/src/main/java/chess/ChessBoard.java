package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard oldBoard) {
        board = new ChessPiece[8][8];
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var position = new ChessPosition(i, j);
                addPiece(position, oldBoard.getPiece(position));
            }
        }
    }

    public ChessPosition getKingPosition(ChessGame.TeamColor team) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var position = new ChessPosition(i, j);
                if (!isEmptyPosition(position)) {
                    var piece = getPiece(position);
                    if (piece.getPieceType() == KING && piece.getTeamColor() == team) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        ChessPiece.PieceType[] order = {ROOK,KNIGHT,BISHOP,QUEEN,KING,BISHOP,KNIGHT,ROOK};
        for (int i = 0; i < 8; i++) {
            addPiece(new ChessPosition(1,i+1), new ChessPiece(ChessGame.TeamColor.WHITE, order[i]));
            addPiece(new ChessPosition(2,i+1), new ChessPiece(ChessGame.TeamColor.WHITE, PAWN));
            addPiece(new ChessPosition(7,i+1), new ChessPiece(ChessGame.TeamColor.BLACK, PAWN));
            addPiece(new ChessPosition(8,i+1), new ChessPiece(ChessGame.TeamColor.BLACK, order[i]));
        }
    }

    public boolean isEmptyPosition(ChessPosition position) {
        return getPiece(position) == null;
    }

    public Collection<ChessPosition> getTeamPositions(ChessGame.TeamColor team) {
        ArrayList<ChessPosition> positions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                var position = new ChessPosition(i, j);
                var piece = getPiece(position);
                if (piece != null && piece.getTeamColor() == team) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
