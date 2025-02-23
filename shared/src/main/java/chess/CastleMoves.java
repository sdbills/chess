package chess;

public class CastleMoves {
    public static ChessMove kingSideCastle(ChessGame game, ChessPosition startPosition) {
        var piece = game.board.getPiece(startPosition);
        var pos1 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()+1);
        var pos2 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()+2);
        if (game.board.isEmptyPosition(pos1) && game.board.isEmptyPosition(pos2)) {
            if (canCastle(game, startPosition, piece, pos1, pos2)) {
                return new ChessMove(startPosition, pos2);
            }
        }
        return null;
    }

    public static ChessMove queenSideCastle(ChessGame game, ChessPosition startPosition) {
        var piece = game.board.getPiece(startPosition);
        var pos1 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-1);
        var pos2 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-2);
        var pos3 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-3);
        if (game.board.isEmptyPosition(pos1) && game.board.isEmptyPosition(pos2) && game.board.isEmptyPosition(pos3)) {
            if (canCastle(game, startPosition, piece, pos1, pos2)) {
                return new ChessMove(startPosition, pos2);
            }
        }
        return null;
    }

    public static void moveRookCastle(ChessGame game, ChessPosition kingPosition, boolean isKingSideCastle) {
        ChessPosition newPosition;
        ChessPosition oldPosition;

        if (isKingSideCastle) {
            newPosition = new ChessPosition(kingPosition.getRow(), 6);
            oldPosition = new ChessPosition(kingPosition.getRow(), 8);
        } else {
            newPosition = new ChessPosition(kingPosition.getRow(), 4);
            oldPosition = new ChessPosition(kingPosition.getRow(), 1);
        }

        game.board.addPiece(newPosition,game.board.getPiece(oldPosition));
        game.board.addPiece(oldPosition,null);
    }

    private static boolean canCastle(ChessGame game, ChessPosition startPosition, ChessPiece piece,
                                     ChessPosition pos1, ChessPosition pos2) {
        var tempGame1 = new ChessGame(game);
        tempGame1.board.addPiece(pos1,piece);
        tempGame1.board.addPiece(startPosition,null);

        var tempGame2 = new ChessGame(game);
        tempGame2.board.addPiece(pos2,piece);
        tempGame2.board.addPiece(startPosition,null);

        return !tempGame1.isInCheck(piece.getTeamColor())
                && !tempGame2.isInCheck(piece.getTeamColor());
    }


}
