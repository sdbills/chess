package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = -1; i < 2; i++) { // One in each direction
            for (int j = -1; j < 2; j++) {
                ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn()+ j);
                ChessMove newMove = new ChessMove(position, newPosition);
                if (newMove.isValidMove(board)) {
                    moves.add(newMove);
                }
            }
        }
        return moves;
    }
}
