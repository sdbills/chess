package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessGame.TeamColor tc = board.getPiece(position).getTeamColor();
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = -1; i < 2; i++) { //Boxes around king
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) { //No movement
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn()+ j);
                if (!newPosition.isValid()) { //check bounds
                    continue;
                }
                ChessPiece piece = board.getPiece(newPosition);
                if (piece == null || tc!=piece.getTeamColor()) {
                    moves.add(new ChessMove(position, newPosition));
                }
            }
        }
        return moves;
    }
}
