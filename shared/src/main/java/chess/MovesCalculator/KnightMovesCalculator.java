package chess.MovesCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        int[] directions = {-1, 1};
        for (int i : directions) {
            for (int j : directions) {
                for (int k : directions) {
                    ChessPosition newPosition;
                    if (k == -1) {
                        newPosition = new ChessPosition(position.getRow()+2*i,position.getColumn()+j);
                    } else {
                        newPosition = new ChessPosition(position.getRow()+i,position.getColumn()+2*j);
                    }
                    var newMove = new ChessMove(position, newPosition);
                    if (newMove.isValidMove(board)) {
                        moves.add(newMove);
                    }
                }
            }
        }
        return moves;
    }
}
