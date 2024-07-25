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
        board.resetBoard();
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
        HashSet<ChessMove> vmoves = new HashSet<>();
        if (board.getPiece(startPosition) == null) {
            System.out.println("returning early!");
            return vmoves;
        }
        TeamColor color = board.getPieceColor(startPosition);
        vmoves.addAll(board.getPieceMoves(startPosition));
        enPresantMove(startPosition).ifPresent(moves::add);
        vmoves.addAll(castleMoves(startPosition));
        HashSet<ChessMove> removes = new HashSet<>();
        for (ChessMove move : vmoves) {
            ChessBoard testBoard = ChessBoard.copy(board);
            makeMoveUnchecked(move, testBoard);
            if (isInCheck(color, testBoard)) {
                System.out.println("removing due to is in check");
                removes.add(move);
            }
        }
        vmoves.removeAll(removes);
        return vmoves;
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
        if (boardToTest.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No piece for move!");
        }
        if (boardToTest == board && turn != boardToTest.getPieceColor(move.getStartPosition())) {
            throw new InvalidMoveException("Out of turn move! ");
        }

        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("Invalid move: " + move);
        }
        makeMoveUnchecked(move, boardToTest);
    }
    private void makeMoveUnchecked(ChessMove move, ChessBoard boardToTest)  {
        ChessPiece piece = new ChessPiece(boardToTest.getPiece(move.getStartPosition()));
        if (move.getPromotionPiece() != null)  {
            piece.setPieceType( move.getPromotionPiece() );
        }
        piece.moved();
        boardToTest.removePiece(move.getStartPosition());
        boardToTest.addPiece(move.getEndPosition(), piece);
        if (boardToTest == board)  {
            moves.add(move);
        }
        if (boardToTest == board) {
            turn = other(turn);
        }
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
        Set<ChessPosition> strikeZone = zoneOfControl(other(teamColor), boardToTest);
        if (kingPos.isEmpty()) {
            return false;
        }
        return strikeZone.containsAll(kingPos);
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
        if( !isInCheck(teamColor) ) {
            return false;
        }

        HashSet<ChessMove> allMoves = (HashSet<ChessMove>) getAllMoves(teamColor, boardToTest);
        HashSet<ChessMove> removes = new HashSet<>();
        for (ChessMove move : allMoves) {
            if ( isInCheck(teamColor, doHypoMove(move)) ) {
                removes.add(move);
            }
        }
        allMoves.removeAll(removes);
        return allMoves.isEmpty();
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
        if (turn != teamColor) {
            return false;
        }
        if (isInCheckmate(teamColor, boardToTest)) {
            return false;
        }
        System.out.println(boardToTest);
        HashSet<ChessMove> allValidMoves = new HashSet<>();
        for (Map.Entry<ChessPosition, ChessPiece> entry : boardToTest.getcPieces(teamColor).entrySet()) {
            System.out.println("entry: " + entry);
            System.out.println("validMoves: " + validMoves(entry.getKey()));
            allValidMoves.addAll(validMoves(entry.getKey()));
        }
        System.out.println(allValidMoves);
        return allValidMoves.isEmpty();
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
        return castleMoves(kingPos, board);
    }
    private Collection<ChessMove> castleMoves(ChessPosition kingPos, ChessBoard boardToTest) {
        ArrayList<ChessMove> cmoves = new ArrayList<>();
        ChessPiece piece = boardToTest.getPiece(kingPos);
        if (piece.getPieceType() != ChessPiece.PieceType.KING) {
            return cmoves;
        }
        else if (piece.isMoved()) {
            return cmoves;
        }
        else if (isInCheck(piece.getTeamColor())) {
            return cmoves;
        }
        for (int dir = -1; dir <= 1; dir+=2) { for (int dist = 1; dist <=4; dist++) {
            ChessPiece relPiece = boardToTest.getPiece(kingPos.rel(0, dir*dist));
            if (relPiece == null) {
                continue;
            }
            else if (relPiece.getPieceType() == ChessPiece.PieceType.ROOK && relPiece.isNotMoved()) {
                if (dist < 3) {
                    break;
                }
                cmoves.add(new ChessMove(
                        kingPos, kingPos.rel(0, dir*2), null, true, false)
                );
            }
        }}
        return cmoves;
    }
    private void makeCastleMove(ChessMove move, ChessBoard boardToTest) throws InvalidMoveException {
        if ( !castleMoves(move.getStartPosition(), boardToTest).contains(move) ) {
            throw new InvalidMoveException("Invalid Castle: " + move);
        }
        makeMove(move, boardToTest);
        ChessPosition kingPos = move.getEndPosition();
        for (int i = -2; i <= 2; i++) {
            int dir = (i < 0) ? -1 : 1;
            if ( boardToTest.getPiece(kingPos.rel(0, i)).getPieceType() == ChessPiece.PieceType.ROOK
            && boardToTest.getPiece(kingPos.rel(0, i)).isNotMoved()) {
                makeMoveUnchecked( new ChessMove(kingPos.rel(0, i), kingPos.rel(0, -dir)), boardToTest );
            }
        }
    }

    private Optional<ChessMove> enPresantMove(ChessPosition pawnPos) {
        return enPresantMove(pawnPos, board);
    }
    private Optional<ChessMove> enPresantMove(ChessPosition pawnPos, ChessBoard boardToTest) {
        Optional<ChessMove> epmove = Optional.empty();
        ChessPiece piece = boardToTest.getPiece(pawnPos);
        if ( piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return epmove;
        }
        if ( moves.isEmpty() ) {
            return epmove;
        }
        ChessMove lastm = moves.getLast();
        if ( boardToTest.getPiece(lastm.getEndPosition()).getPieceType() != ChessPiece.PieceType.PAWN ) {
            return epmove;
        }
        ChessPosition lmend = lastm.getEndPosition();
        ChessPosition lmstart = lastm.getStartPosition();
        if (!( Math.abs(lmend.getRow() - lmstart.getRow()) > 1 )) {
            return epmove;
        }
        if ( lmend.getRow() != pawnPos.getRow() )  {
            return epmove;
        }
        if ( Math.abs( lmend.getColumn() - pawnPos.getColumn() ) != 1 ) {
            return epmove;
        }
        int dir = 1; if (boardToTest.getPiece(pawnPos).getTeamColor() == TeamColor.BLACK) {
            dir = -1;
        }
        ChessMove moveEnPresant = new ChessMove(pawnPos, lmend.rel(dir, 0), null, false, true);
        epmove = Optional.of( moveEnPresant );
        return epmove;
    }
    private void makeEnPresantMove(ChessMove move, ChessBoard board) {

    }

    private ChessBoard doHypoMove(ChessMove move) {
        return doHypoMove(move, board);
    }
    private ChessBoard doHypoMove(ChessMove move, ChessBoard testBoard) {
        ChessBoard newBoard = ChessBoard.copy(testBoard);
        makeMoveUnchecked(move, newBoard);
        return newBoard;
    }

    private Set<ChessMove> getAllMoves (TeamColor team, ChessBoard testBoard) {
        HashSet<ChessMove> allMoves = new HashSet<>();
        Map<ChessPosition, ChessPiece> cpieces = testBoard.getcPieces(team);
        for (Map.Entry<ChessPosition, ChessPiece> entry : cpieces.entrySet()) {
            allMoves.addAll( entry.getValue().pieceMoves(testBoard, entry.getKey()) );
        }
        return allMoves;
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
        if (false) {
            System.out.println(zoneToString(zone, testBoard));
        }
        return zone;
    }

    private String zoneToString (Set<ChessPosition> zone, ChessBoard testBoard) {
        StringBuilder sb = new StringBuilder();
        sb.append("Zone{\n");
        for (int row = 1; row <= 8; row++) { for (int col = 1; col <= 8; col++) {
                sb.append("|");
                if (testBoard.getPieceAt(row, col) == null) {
                    sb.append(" ");
                }
                else {
                    if (testBoard.getPieceAt(row, col).getTeamColor() == TeamColor.WHITE) {
                        sb.append("w");
                    }
                    else {
                        sb.append("b");
                    }
                }
                if (zone.contains(new ChessPosition(row, col))) {
                    sb.append("X");
                }
                else {
                    sb.append(" ");
                }
            }
            sb.append("|");
            if (row != 8) {
                sb.append("\n");
            }
        }
        sb.append(" }\n");
        return sb.toString();
    }



    private TeamColor other(TeamColor team) {
        switch (team) {
            case WHITE -> { return TeamColor.BLACK; }
            case BLACK -> { return TeamColor.WHITE; }
            default -> {return null; }
        }
    }
}
