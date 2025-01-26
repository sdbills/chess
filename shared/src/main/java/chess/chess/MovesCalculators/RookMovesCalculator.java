package chess.MovesCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;


public class RookMovesCalculator extends PiecesMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var moves = new ArrayList<ChessMove>();
        int[] directions = {-1,1};
        for (var n : directions) {
            for (var m : directions) {
                ChessPosition newPosition;
                int i = 1;
                do {
                    if (n == -1) {
                        newPosition = new ChessPosition(position.getRow()+i*m, position.getColumn());
                    } else {
                        newPosition = new ChessPosition(position.getRow(), position.getColumn()+i*m);
                    }
                    var newMove = createLegalMove(board,position,newPosition);
                    if (newMove != null) {
                        moves.add(newMove);
                    }
                    i++;
                } while (newPosition.isValid() && board.getPiece(newPosition) == null);
            }
        }

        return moves;
    }
}
