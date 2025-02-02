package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor teamTurn;
    boolean bkCanCastle;
    boolean bqCanCastle;
    boolean wkCanCastle;
    boolean wqCanCastle;
    ChessPosition enPassantPosition;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        board.resetBoard();
        bkCanCastle = true;
        bqCanCastle = true;
        wkCanCastle = true;
        wqCanCastle = true;
    }

    public ChessGame(ChessGame game) {
        board = new ChessBoard(game.board);
        teamTurn = game.teamTurn;
        bkCanCastle = game.bkCanCastle;
        bqCanCastle = game.bqCanCastle;
        wkCanCastle = game.wkCanCastle;
        wqCanCastle = game.wqCanCastle;
        enPassantPosition = game.enPassantPosition;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.isEmptyPosition(startPosition)) {
            return null;
        }
        var piece = board.getPiece(startPosition);
        var team = piece.getTeamColor();
        var moves = piece.pieceMoves(board, startPosition);
        var valMoves = new ArrayList<ChessMove>();
        for (var move : moves) {
            var tempGame = new ChessGame(this);
            tempGame.board.addPiece(move.getEndPosition(),piece);
            tempGame.board.addPiece(move.getStartPosition(),null);
            if (!tempGame.isInCheck(team)) {
                valMoves.add(move);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            var castleMoves = validCastleMoves(startPosition);
            if (castleMoves != null) {
                valMoves.addAll(castleMoves);
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            var enPassant = validEnPassant(startPosition);
            if (enPassant != null) {
                valMoves.add(enPassant);
            }
        }

        return valMoves;
    }

    public Collection<ChessMove> allValidMoves(TeamColor team) {
        var positions = board.getTeamPositions(team);
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (var position : positions) {
            moves.addAll(validMoves(position));
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPosition = move.getStartPosition();
        var endPosition = move.getEndPosition();

        if (board.isEmptyPosition(startPosition)
                || !validMoves(startPosition).contains(move)
                || board.getPiece(startPosition).getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        ChessPiece piece = board.getPiece(startPosition);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING) {  //Check for Castling
            if (endPosition.getColumn()-startPosition.getColumn() == 2) { //KingSide
                moveRookCastle(endPosition,true);
            } else if (endPosition.getColumn()-startPosition.getColumn() == -2) { //QueenSide
                moveRookCastle(endPosition,false);
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) { //Check for En Passant
            if (move.equals(validEnPassant(startPosition))) {
                board.addPiece(enPassantPosition, null);
            }
        }

        board.addPiece(endPosition,piece);
        board.addPiece(startPosition,null);

        enPassantPosition = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) { //Check for advance to pass
            if (Math.abs(endPosition.getRow()-startPosition.getRow()) == 2) {
            enPassantPosition = endPosition;
            }
        }

        updateCastleFlags();
        teamTurn = otherTeam(teamTurn);
    }

    private TeamColor otherTeam(TeamColor team) {
        if (team == TeamColor.BLACK) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    private Collection<ChessMove> allTeamMoves(TeamColor team) {
        var moves = new ArrayList<ChessMove>();
        var positions = board.getTeamPositions(team);
        for (var position : positions) {
            moves.addAll(board.getPiece(position).pieceMoves(board,position));
        }
        return moves;
    }

    private Collection<ChessMove> validCastleMoves(ChessPosition startPosition) {
        if (board.isEmptyPosition(startPosition)) {
            return null;
        }
        var piece = board.getPiece(startPosition);
        var team = piece.getTeamColor();
        if (isInCheck(team)) {
            return null;
        }
        ArrayList<ChessMove> moves = new ArrayList<>();
        if ((team == TeamColor.BLACK && bkCanCastle)
                || ((team == TeamColor.WHITE) && wkCanCastle)) {
            var move = kingSideCastle(startPosition);
            if (move != null) {
                moves.add(move);
            }
        }
        if ((team == TeamColor.BLACK && bqCanCastle)
                || (team == TeamColor.WHITE && wqCanCastle)) {
            var move = queenSideCastle(startPosition);
            if (move != null) {
                moves.add(move);
            }
        }
        return moves;
    }

    private ChessMove kingSideCastle(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        var pos1 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()+1);
        var pos2 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()+2);
        if (board.isEmptyPosition(pos1) && board.isEmptyPosition(pos2)) {
            if (canCastle(startPosition, piece, pos1, pos2)) {
                return new ChessMove(startPosition, pos2);
            }
        }
        return null;
    }

    private ChessMove queenSideCastle(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        var pos1 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-1);
        var pos2 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-2);
        var pos3 = new ChessPosition(startPosition.getRow(),startPosition.getColumn()-3);
        if (board.isEmptyPosition(pos1) && board.isEmptyPosition(pos2) && board.isEmptyPosition(pos3)) {
            if (canCastle(startPosition, piece, pos1, pos2)) {
                return new ChessMove(startPosition, pos2);
            }
        }
        return null;
    }

    private boolean canCastle(ChessPosition startPosition, ChessPiece piece,
                              ChessPosition pos1, ChessPosition pos2) {
        var tempGame1 = new ChessGame(this);
        tempGame1.board.addPiece(pos1,piece);
        tempGame1.board.addPiece(startPosition,null);

        var tempGame2 = new ChessGame(this);
        tempGame2.board.addPiece(pos2,piece);
        tempGame2.board.addPiece(startPosition,null);

        return !tempGame1.isInCheck(piece.getTeamColor())
                && !tempGame2.isInCheck(piece.getTeamColor());
    }

    private void updateCastleFlags() {
        var wqRook = board.getPiece(new ChessPosition(1, 1));
        var wkRook = board.getPiece(new ChessPosition(1, 8));
        var wKing = board.getPiece(new ChessPosition(1, 5));
        if (wKing == null || wKing.getTeamColor() != TeamColor.WHITE ||
                wKing.getPieceType() != ChessPiece.PieceType.KING) {
            wqCanCastle = false;
            wkCanCastle = false;
        } else {
            if (wqRook == null  || wqRook.getPieceType() != ChessPiece.PieceType.ROOK
                    || wqRook.getTeamColor() != TeamColor.WHITE) {
                wqCanCastle = false;
            }
            if (wkRook == null || wkRook.getPieceType() != ChessPiece.PieceType.ROOK
                    || wkRook.getTeamColor() != TeamColor.WHITE) {
                wkCanCastle = false;
            }
        }

        var bqRook = board.getPiece(new ChessPosition(8, 1));
        var bkRook = board.getPiece(new ChessPosition(8, 8));
        var bKing = board.getPiece(new ChessPosition(8, 5));
        if (bKing == null || bKing.getTeamColor() != TeamColor.BLACK ||
                bKing.getPieceType() != ChessPiece.PieceType.KING) {
            bqCanCastle = false;
            bkCanCastle = false;
        } else {
            if (bqRook == null || bqRook.getPieceType() != ChessPiece.PieceType.ROOK
                    || bqRook.getTeamColor() != TeamColor.BLACK) {
                bqCanCastle = false;
            }
            if (bkRook == null || bkRook.getPieceType() != ChessPiece.PieceType.ROOK
                    || bkRook.getTeamColor() != TeamColor.BLACK) {
                bkCanCastle = false;
            }
        }
    }

    private void moveRookCastle(ChessPosition kingPosition, boolean isKingSideCastle) {
        ChessPosition newPosition;
        ChessPosition oldPosition;

        if (isKingSideCastle) {
            newPosition = new ChessPosition(kingPosition.getRow(), 6);
            oldPosition = new ChessPosition(kingPosition.getRow(), 8);
        } else {
            newPosition = new ChessPosition(kingPosition.getRow(), 4);
            oldPosition = new ChessPosition(kingPosition.getRow(), 1);
        }

        board.addPiece(newPosition,board.getPiece(oldPosition));
        board.addPiece(oldPosition,null);
    }

    private ChessMove validEnPassant(ChessPosition startPosition) {
        if (enPassantPosition == null) {
            return null;
        }

        var team = board.getPiece(startPosition).getTeamColor();
        int d = 1;
        if (team == TeamColor.BLACK) {
            d = -1;
        }

        if (startPosition.getRow() == enPassantPosition.getRow()) {
            if (Math.abs(startPosition.getColumn() - enPassantPosition.getColumn()) == 1) {
                var newPosition = new ChessPosition(enPassantPosition.getRow()+d, enPassantPosition.getColumn());
                var tempGame = new ChessGame(this);
                tempGame.board.addPiece(newPosition,board.getPiece(startPosition));
                tempGame.board.addPiece(startPosition,null);
                tempGame.board.addPiece(enPassantPosition,null);
                if (!tempGame.isInCheck(team)) {
                    return new ChessMove(startPosition, newPosition);
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        var moves = allTeamMoves(otherTeam(teamColor));
        for (var move : moves) {
            if (move.getEndPosition().equals(board.getKingPosition(teamColor))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return allValidMoves(teamColor).isEmpty() && isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return allValidMoves(teamColor).isEmpty() && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        wqCanCastle = true;
        wkCanCastle = true;
        bkCanCastle = true;
        bqCanCastle = true;
        updateCastleFlags();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && getTeamTurn() == chessGame.getTeamTurn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), getTeamTurn());
    }
}
