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
    boolean isOver;

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        board.resetBoard();
        bkCanCastle = true;
        bqCanCastle = true;
        wkCanCastle = true;
        wqCanCastle = true;
        isOver = false;
    }

    /**
     * Copy constructor for ChessGame
     * @param game ChessGame to be copied
     */
    public ChessGame(ChessGame game) {
        board = new ChessBoard(game.board);
        teamTurn = game.teamTurn;
        bkCanCastle = game.bkCanCastle;
        bqCanCastle = game.bqCanCastle;
        wkCanCastle = game.wkCanCastle;
        wqCanCastle = game.wqCanCastle;
        enPassantPosition = game.enPassantPosition;
        isOver = game.isOver;
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
        if (board.isEmptyPosition(startPosition) || isOver) {
            return null;
        }

        var piece = board.getPiece(startPosition);
        var team = piece.getTeamColor();
        var moves = piece.pieceMoves(board, startPosition);
        var valMoves = new ArrayList<ChessMove>();
        //Move doesn't put into check
        for (var move : moves) {
            var tempGame = new ChessGame(this);
            tempGame.board.addPiece(move.getEndPosition(),piece);
            tempGame.board.addPiece(move.getStartPosition(),null);
            if (!tempGame.isInCheck(team)) {
                valMoves.add(move);
            }
        }

        //Check for castling and en passant moves
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

    /**
     * Gets all the valid moves for each piece of the specified team
     *
     * @param team the team to get moves for
     * @return set of all valid moves
     */
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

        //Check for invalid moves
        if (board.isEmptyPosition(startPosition)
                || !validMoves(startPosition).contains(move)
                || board.getPiece(startPosition).getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        //Get the piece type to move or promote
        ChessPiece piece = board.getPiece(startPosition);
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(),move.getPromotionPiece());
        }

        //Castling and en passant moves
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {  //Check for Castling
            if (endPosition.getColumn()-startPosition.getColumn() == 2) { //KingSide
                CastleMoves.moveRookCastle(this, endPosition,true);
            } else if (endPosition.getColumn()-startPosition.getColumn() == -2) { //QueenSide
                CastleMoves.moveRookCastle(this, endPosition,false);
            }
        } else if (piece.getPieceType() == ChessPiece.PieceType.PAWN) { //Check for En Passant
            if (move.equals(validEnPassant(startPosition))) {
                board.addPiece(enPassantPosition, null);
            }
        }

        //Update the board
        board.addPiece(endPosition,piece);
        board.addPiece(startPosition,null);

        //Update game status (en passant, castling, team turn)
        enPassantPosition = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) { //Check for advance to pass
            if (Math.abs(endPosition.getRow()-startPosition.getRow()) == 2) {
            enPassantPosition = endPosition;
            }
        }
        updateCastleFlags();
        teamTurn = otherTeam(teamTurn);
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
        enPassantPosition = null;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public void endGame() {
        isOver = true;
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
        //King-side castles
        if ((team == TeamColor.BLACK && bkCanCastle) ||
                ((team == TeamColor.WHITE) && wkCanCastle)) {
            var move = CastleMoves.kingSideCastle(this, startPosition);
            if (move != null) {
                moves.add(move);
            }
        }
        //Queen-side castles
        if ((team == TeamColor.BLACK && bqCanCastle) ||
                (team == TeamColor.WHITE && wqCanCastle)) {
            var move = CastleMoves.queenSideCastle(this, startPosition);
            if (move != null) {
                moves.add(move);
            }
        }
        return moves;
    }

    private void updateCastleFlags() {
        //White castle flags
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

        //Black castle flags
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
}
