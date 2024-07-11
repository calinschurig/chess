package chess;

import java.util.*;
import java.lang.Math;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    ArrayList<ChessMove> moves;
    TeamColor turn;


    public ChessGame() {
        board = new ChessBoard();
        moves = new ArrayList<>();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Set<ChessPosition> kingPos = board.getcPiecesOfType(ChessPiece.PieceType.KING, teamColor).keySet();
        Set<ChessPosition> strikeZone = zoneOfControl(other(teamColor));
        if (strikeZone.containsAll(kingPos)) return true;
        else return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if( !isInCheck(teamColor) ) return false;
        Map<ChessPosition, ChessPiece> kingPos = board.getcPiecesOfType(ChessPiece.PieceType.KING, teamColor);
        HashSet<ChessMove> kingMoves = new HashSet<>();
        HashSet<ChessPosition> kingEnds = new HashSet<>();
        for (Map.Entry<ChessPosition, ChessPiece> entry : kingPos.entrySet()) {
            kingMoves.addAll( entry.getValue().pieceMoves(board, entry.getKey()) );
        }
        for (ChessMove move : kingMoves) {
            kingEnds.add(move.getEndPosition());
        }

        Set<ChessPosition> strikeZone = zoneOfControl(other(teamColor));
        if (strikeZone.containsAll(kingEnds)) return true;
        else return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (zoneOfControl(teamColor).isEmpty()) return true;
        else return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        ChessGame.this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<ChessMove> castleMoves(ChessPosition kingPos) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(kingPos);
        if (piece.getPieceType() != ChessPiece.PieceType.KING) return moves;
        else if (piece.isMoved()) return moves;
        else if (isInCheck(piece.getTeamColor())) return moves;
        for (int dir = -1; dir <= 1; dir+=2) { for (int dist = 1; dist <=4; dist++) {
            ChessPiece relPiece = board.getPiece(kingPos.rel(0, dir*dist));
            if (relPiece == null) continue;
            else if (relPiece.getPieceType() == ChessPiece.PieceType.ROOK && relPiece.isNotMoved()) {
                if (dist < 3) break;
                moves.add(new ChessMove(
                        kingPos, kingPos.rel(0, dir*2), null, true, false)
                );
            }
        }}
        return moves;
    }

    private Optional<ChessMove> enPresantMove(ChessPosition pawnPos) {
        Optional<ChessMove> move = Optional.empty();
        ChessPiece piece = board.getPiece(pawnPos);
        if ( piece.getPieceType() != ChessPiece.PieceType.PAWN) return move;
        ChessMove lastm = moves.getLast();
        if ( board.getPiece(lastm.getEndPosition()).getPieceType() != ChessPiece.PieceType.PAWN ) return move;
        ChessPosition lmend = lastm.getEndPosition();
        ChessPosition lmstart = lastm.getStartPosition();
        if (!( Math.abs(lmend.getRow() - lmstart.getRow()) > 1 )) return move;
        if ( lmend.getRow() != pawnPos.getRow() )  return move;
        if ( Math.abs( lmend.getColumn() - pawnPos.getColumn() ) != 1 ) return move;
        int dir = 1; if (board.getPiece(pawnPos).getTeamColor() == TeamColor.BLACK) dir = -1;
        ChessMove moveEnPresant = new ChessMove(pawnPos, lmend.rel(dir, 0), null, false, true);
        move = Optional.of( moveEnPresant );
        return move;
    }

    private Set<ChessPosition> zoneOfControl(TeamColor team) {
        HashSet<ChessPosition> zone = HashSet.newHashSet(32);
        Map<ChessPosition, ChessPiece> cpieces =  board.getcPieces(team);
        for (Map.Entry<ChessPosition, ChessPiece> entry : cpieces.entrySet()) {
            Collection<ChessMove> moves = entry.getValue().myZoneOfControl(board, entry.getKey());
            ArrayList<ChessPosition> ends = new ArrayList<>();
            moves.forEach(move -> ends.add(move.getEndPosition()));
            zone.addAll(ends);
        }
        return zone;
    }

    private TeamColor other(TeamColor team) {
        switch (team) {
            case WHITE -> { return TeamColor.BLACK; }
            case BLACK -> { return TeamColor.WHITE; }
            default -> {return null; }
        }
    }
}
