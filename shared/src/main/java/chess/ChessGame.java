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
//    ChessBoard testBoard;
    ArrayList<ChessMove> moves;
    TeamColor turn;


    public ChessGame() {
        board = new ChessBoard();
//        testBoard = new ChessBoard(board);
        moves = new ArrayList<>();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return turn; }

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
        Set<ChessMove> vmoves = new HashSet<>();
        vmoves.addAll(board.getPieceMoves(startPosition));
        enPresantMove(startPosition).ifPresent(moves::add);
        vmoves.addAll(castleMoves(startPosition));
        for (ChessMove move : vmoves) {
            ChessBoard testBoard = new ChessBoard(board);
            makeMoveUnchecked(move, testBoard);
            if (isInCheck(board.getPieceColor(startPosition), testBoard)) {
                vmoves.remove(move);
            }
        }

        return vmoves;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        makeMove(move, board);
    }
    public void makeMove(ChessMove move, ChessBoard boardToTest) throws InvalidMoveException {
        if (!validMoves(move.getStartPosition()).contains(move)) throw new InvalidMoveException("Invalid move: " + move.toString());
        makeMoveUnchecked(move, boardToTest);
    }
    private void makeMoveUnchecked(ChessMove move, ChessBoard boardToTest)  {
//        if (!validMoves(move.getStartPosition()).contains(move)) throw new InvalidMoveException("Invalid move: " + move.toString());
        ChessPiece piece = new ChessPiece(boardToTest.getPiece(move.getStartPosition()));
        if (move.getPromotionPiece() != null) piece.setPieceType( move.getPromotionPiece() );
        piece.moved();
        boardToTest.removePiece(move.getStartPosition());
        boardToTest.addPiece(move.getEndPosition(), piece);
        if (boardToTest == board) moves.add(move);
    }



    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }
    public boolean isInCheck(TeamColor teamColor, ChessBoard boardToTest) {
        Set<ChessPosition> kingPos = boardToTest.getcPiecesOfType(ChessPiece.PieceType.KING, teamColor).keySet();
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
        return isInCheckmate(teamColor, board);
    }
    public boolean isInCheckmate(TeamColor teamColor, ChessBoard boardToTest) {
        if( !isInCheck(teamColor) ) return false;
        Map<ChessPosition, ChessPiece> kingPos = boardToTest.getcPiecesOfType(ChessPiece.PieceType.KING, teamColor);
        HashSet<ChessMove> kingMoves = new HashSet<>();
        HashSet<ChessPosition> kingEnds = new HashSet<>();
        for (Map.Entry<ChessPosition, ChessPiece> entry : kingPos.entrySet()) {
            kingMoves.addAll( entry.getValue().pieceMoves(boardToTest, entry.getKey()) );
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
        return isInStalemate(teamColor, board);
    }
    public boolean isInStalemate(TeamColor teamColor, ChessBoard boardToTest) {
        if (zoneOfControl(teamColor, boardToTest).isEmpty()) return true;
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
//    public void resetTestBoard() {
//        testBoard = new ChessBoard(board);
//    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private Collection<ChessMove> castleMoves(ChessPosition kingPos) {
        return castleMoves(kingPos, board);
    }
    private Collection<ChessMove> castleMoves(ChessPosition kingPos, ChessBoard boardToTest) {
        ArrayList<ChessMove> cmoves = new ArrayList<>();
        ChessPiece piece = boardToTest.getPiece(kingPos);
        if (piece.getPieceType() != ChessPiece.PieceType.KING) return cmoves;
        else if (piece.isMoved()) return cmoves;
        else if (isInCheck(piece.getTeamColor())) return cmoves;
        for (int dir = -1; dir <= 1; dir+=2) { for (int dist = 1; dist <=4; dist++) {
            ChessPiece relPiece = boardToTest.getPiece(kingPos.rel(0, dir*dist));
            if (relPiece == null) continue;
            else if (relPiece.getPieceType() == ChessPiece.PieceType.ROOK && relPiece.isNotMoved()) {
                if (dist < 3) break;
                cmoves.add(new ChessMove(
                        kingPos, kingPos.rel(0, dir*2), null, true, false)
                );
            }
        }}
        return cmoves;
    }
    private void makeCastleMove(ChessMove move, ChessBoard boardToTest) throws InvalidMoveException {
        if ( !castleMoves(move.getStartPosition(), boardToTest).contains(move) ) return;
        makeMove(move, boardToTest);
        //TODO: move the rook as well.
        return;
    }

    private Optional<ChessMove> enPresantMove(ChessPosition pawnPos) {
        return enPresantMove(pawnPos, board);
    }
    private Optional<ChessMove> enPresantMove(ChessPosition pawnPos, ChessBoard boardToTest) {
        Optional<ChessMove> epmove = Optional.empty();
        ChessPiece piece = boardToTest.getPiece(pawnPos);
        if ( piece.getPieceType() != ChessPiece.PieceType.PAWN) return epmove;
        ChessMove lastm = moves.getLast();
        if ( boardToTest.getPiece(lastm.getEndPosition()).getPieceType() != ChessPiece.PieceType.PAWN ) return epmove;
        ChessPosition lmend = lastm.getEndPosition();
        ChessPosition lmstart = lastm.getStartPosition();
        if (!( Math.abs(lmend.getRow() - lmstart.getRow()) > 1 )) return epmove;
        if ( lmend.getRow() != pawnPos.getRow() )  return epmove;
        if ( Math.abs( lmend.getColumn() - pawnPos.getColumn() ) != 1 ) return epmove;
        int dir = 1; if (boardToTest.getPiece(pawnPos).getTeamColor() == TeamColor.BLACK) dir = -1;
        ChessMove moveEnPresant = new ChessMove(pawnPos, lmend.rel(dir, 0), null, false, true);
        epmove = Optional.of( moveEnPresant );
        return epmove;
    }
    private void makeEnPresantMove(ChessMove move, ChessBoard board) {

    }

    private Set<ChessPosition> zoneOfControl(TeamColor team) {
        return zoneOfControl(team, board);
    }
    private Set<ChessPosition> zoneOfControl(TeamColor team, ChessBoard testBoard) {
        HashSet<ChessPosition> zone = HashSet.newHashSet(32);
        Map<ChessPosition, ChessPiece> cpieces =  testBoard.getcPieces(team);
        for (Map.Entry<ChessPosition, ChessPiece> entry : cpieces.entrySet()) {
            Collection<ChessMove> moves = entry.getValue().myZoneOfControl(testBoard, entry.getKey());
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
