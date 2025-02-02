package chess.movescalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public abstract class PiecesMovesCalculator {
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    public ChessMove createLegalMove(ChessBoard board, ChessPosition oldPosition, ChessPosition newPosition) {
        var newMove = new ChessMove(oldPosition,newPosition);
        if (newMove.isLegitMove()) {
            if (board.isEmptyPosition(newPosition) || board.getPiece(newPosition).getTeamColor() != board.getPiece(oldPosition).getTeamColor()) {
                return newMove;
            }
        }
        return null;
    }
}
