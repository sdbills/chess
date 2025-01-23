package chess;

import java.util.Collection;
import java.util.ArrayList;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>(new BishopMovesCalculator().pieceMoves(board, position));
        moves.addAll(new RookMovesCalculator().pieceMoves(board, position));
        return moves;
    }
}
