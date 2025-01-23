package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int i = 1;
        ChessPosition newPosition;
        int[] directions = {-1,1};
        for (int n = 0; n < 2; n++) {
            for (int d : directions) {
                do {
                    if (n == 1) {
                        newPosition = new ChessPosition(position.getRow() + (i * d), position.getColumn());
                    } else {
                        newPosition = new ChessPosition(position.getRow(), position.getColumn() + (i * d));
                    }

                    ChessMove newMove = new ChessMove(position, newPosition);
                    if (newMove.isValidMove(board)) {
                        moves.add(newMove);
                    }
                    i++;
                }
                while (newPosition.isValid() && board.getPiece(newPosition) == null);
                i = 1;
            }
        }
        return moves;
    }
}