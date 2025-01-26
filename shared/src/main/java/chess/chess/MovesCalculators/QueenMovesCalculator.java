package chess.MovesCalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;


import java.util.Collection;


public class QueenMovesCalculator extends PiecesMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var moves = new RookMovesCalculator().pieceMoves(board, position);
        moves.addAll(new BishopMovesCalculator().pieceMoves(board, position));
        return moves;
    }
}
