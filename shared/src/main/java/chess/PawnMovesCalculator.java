package chess;

import java.util.Collection;
import java.util.ArrayList;

import static chess.ChessPiece.*;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        int d;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            d = 1;
        } else {
            d = -1;
        }

        for (int i = -1; i < 2; i++) {
            var newPosition = new ChessPosition(position.getRow()+d, position.getColumn()+i);
            var newMove = new ChessMove(position, newPosition);
            if (newMove.isValidMove(board)) {
                if ((board.getPiece(newPosition) == null && i == 0)
                        || (board.getPiece(newPosition) != null && i != 0)) { //Capture rules
                    if (newPosition.getRow() == 1 || newPosition.getRow() == 8) { //promote
                        moves.addAll(makePromotionMoves(newMove));
                    } else {
                        moves.add(newMove);
                    }
                }
                if ((position.getRow() == 2 || position.getRow() == 7)
                        && i == 0 && board.getPiece(newPosition) == null) { //First move jump
                    var jumpPosition = new ChessPosition(position.getRow() + 2 * d, position.getColumn());
                    if (jumpPosition.isValid() && board.getPiece(jumpPosition) == null) {
                        moves.add(new ChessMove(position, jumpPosition));
                    }
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> makePromotionMoves(ChessMove move) {
        PieceType[] promotions = {PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT, PieceType.QUEEN};
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (var promo : promotions) {
            moves.add(new ChessMove(move.getStartPosition(), move.getEndPosition(), promo));
        }
        return moves;
    }

}
