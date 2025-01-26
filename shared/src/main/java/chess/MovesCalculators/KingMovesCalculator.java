package chess.MovesCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;


public class KingMovesCalculator extends PiecesMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var moves = new ArrayList<ChessMove>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                var newPosition = new ChessPosition(position.getRow() + i, position.getColumn() + j);
                var newMove = createLegalMove(board,position, newPosition);
                if (newMove != null) {
                    moves.add(newMove);
                }
            }
        }
        return moves;
    }
}
