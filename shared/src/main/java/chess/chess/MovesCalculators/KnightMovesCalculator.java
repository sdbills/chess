package chess.MovesCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;


public class KnightMovesCalculator extends PiecesMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var moves = new ArrayList<ChessMove>();
        int[] directions = {-1,1};
        for (var n : directions) {
            for (var m : directions) {
                for (var p : directions) {
                    ChessPosition newPosition;
                    if (p == 1) {
                        newPosition = new ChessPosition(position.getRow()+2*n, position.getColumn()+m);
                    } else {
                        newPosition = new ChessPosition(position.getRow()+n, position.getColumn()+2*m);
                    }
                    var newMove = createLegalMove(board,position,newPosition);
                    if (newMove != null) {
                        moves.add(newMove);
                    }
                }
            }
        }
        return moves;
    }
}
