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

    public ChessGame() {
        board = new ChessBoard();
        teamTurn = TeamColor.WHITE;
        board.resetBoard();
    }

    public ChessGame(ChessGame game) {
        board = new ChessBoard(game.board);
        teamTurn = game.teamTurn;
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

        if (board.isEmptyPosition(startPosition)
                || !validMoves(startPosition).contains(move)
                || board.getPiece(startPosition).getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        } else {
            board.addPiece(move.getEndPosition(),board.getPiece(startPosition));
            board.addPiece(startPosition,null);
        }
        changeTeam();
    }

    private TeamColor otherTeam(TeamColor team) {
        if (team == TeamColor.BLACK) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    private void changeTeam() {
        teamTurn = otherTeam(teamTurn);
    }

    private Collection<ChessMove> allTeamMoves(TeamColor team) {
        var moves = new ArrayList<ChessMove>();
        var positions = board.getTeamPositions(team);
        for (var position : positions) {
            moves.addAll(board.getPiece(position).pieceMoves(board,position));
        }
        return moves;
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
