package chess.MovesCalculators;

import chess.*;
import chess.ChessPiece.PieceType;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PiecesMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        var moves = new ArrayList<ChessMove>();
        for (int i = -1; i < 2; i++) {
            ChessPosition newPosition;
            int d = 1;
            if (board.getPiece(position).getTeamColor() == ChessGame.TeamColor.BLACK) {
                d = -1;
            }
            newPosition = new ChessPosition(position.getRow()+d,position.getColumn()+i);
            var newMove = createLegalMove(board,position,newPosition);
            if (newMove != null) {
                if (newPosition.getRow() == 1 || newPosition.getRow() == 8) {
                    moves.addAll(createPromotionMoves(newMove));
                } else {
                    moves.add(newMove);
                }
            }

            if ((position.getRow() == 2 || position.getRow() == 7) && i == 0 && board.getPiece(newPosition) == null) {
                newPosition = new ChessPosition(position.getRow()+2*d,position.getColumn());
                newMove = createLegalMove(board,position,newPosition);
                if (newMove != null) {
                    moves.add(newMove);
                }
            }
        }

        return moves;
    }

    @Override
    public ChessMove createLegalMove(ChessBoard board, ChessPosition oldPosition, ChessPosition newPosition) {
        var newMove = new ChessMove(oldPosition,newPosition);
        if (newMove.isValidMove()) {
            if (board.getPiece(newPosition) == null && newPosition.getColumn() == oldPosition.getColumn()) {
                return newMove;
            }
            if (newPosition.getColumn() != oldPosition.getColumn() && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != board.getPiece(oldPosition).getTeamColor()) {
                return newMove;
            }
        }
        return null;
    }

    public Collection<ChessMove> createPromotionMoves(ChessMove move) {
        PieceType[] promos = {PieceType.ROOK, PieceType.BISHOP, PieceType.QUEEN, PieceType.KNIGHT};
        var moves = new ArrayList<ChessMove>();
        for (PieceType p : promos) {
            moves.add(new ChessMove(move.getStartPosition(),move.getEndPosition(),p));
        }
        return moves;
    }
}
