package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int i = 1;
        ChessPosition newPosition;
        int[] directions = {-1,1};
        for (int n : directions) {
            for (int d : directions) {
                do {
                    newPosition = new ChessPosition(position.getRow()+(i*n), position.getColumn()+(i*d));

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