package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece.PieceType[] promotions = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.QUEEN};
        int d;
        if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.WHITE) {
            d = 1;
        } else {
            d = -1;
        }

        for (int i = -1; i < 2; i++) {
            ChessPosition newPosition = new ChessPosition(position.getRow()+d, position.getColumn()+i);
            if (newPosition.isValid()) {
                var newMove = new ChessMove(position,newPosition);
                if (newMove.isValidMove(board)) {
                    if ((board.getPiece(newPosition) == null && i == 0) || (board.getPiece(newPosition) != null && i !=0)) { //Capture rules
                        if (newPosition.getRow() == 1 || newPosition.getRow() == 8) { //promote
                            for (var promo : promotions) {
                                moves.add(new ChessMove(position,newMove.getEndPosition(),promo));
                            }
                        } else moves.add(newMove);

                        if (i == 0 && (position.getRow() == 2 || position.getRow() == 7)) { //First move
                            var jumpPosition = new ChessPosition(position.getRow() + 2 * d, position.getColumn());
                            if (jumpPosition.isValid() && board.getPiece(jumpPosition) == null) {
                                moves.add(new ChessMove(position, jumpPosition));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}
