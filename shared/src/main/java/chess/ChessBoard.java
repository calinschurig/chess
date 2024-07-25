package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board;
    HashMap<ChessPosition, ChessPiece> pieces;

    public ChessBoard() {
        board = new ChessPiece[8][8];
        pieces = HashMap.newHashMap(50);
    }

    public static ChessBoard copy(ChessBoard another) {
        ChessBoard newBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
            ChessPosition pos = new ChessPosition(i+1, j+1);
            if (another.board[i][j] == null) newBoard.board[i][j] = null;
            else newBoard.addPiece(pos, new ChessPiece( another.board[i][j] ) );
        }}
        return newBoard;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
        if (piece != null) pieces.put(position, piece);
    }
    public ChessPiece removePiece(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        board[position.getRow()-1][position.getColumn()-1] = null;
        pieces.remove(position);
        return piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }
    public ChessPiece getPieceAt(int row, int col) {
        return board[row-1][col-1];
    }

    public Collection<ChessMove> getPieceMoves(ChessPosition startPosition) {
        if ( getPiece(startPosition) == null ) return new ArrayList<ChessMove>();
        return getPiece(startPosition).pieceMoves(this, startPosition);
    }

    public ChessGame.TeamColor getPieceColor(ChessPosition startPosition) {
        return getPiece(startPosition).getTeamColor();
    }

    public Map<ChessPosition, ChessPiece> getcPieces(ChessGame.TeamColor team) {
        HashMap<ChessPosition, ChessPiece> cpieces = HashMap.newHashMap(16);
        for (Map.Entry<ChessPosition, ChessPiece> entry : pieces.entrySet()) {
            if (entry.getValue().getTeamColor() == team)
                cpieces.put(entry.getKey(), entry.getValue());
        }
        return cpieces;
    }
    public Map<ChessPosition, ChessPiece> getcPiecesOfType(ChessPiece.PieceType type, ChessGame.TeamColor teamColor) {
        return getcPiecesOfType(type, true, teamColor);
    }
    private Map<ChessPosition, ChessPiece> getcPiecesOfType(ChessPiece.PieceType type, boolean isByTeam, ChessGame.TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> tcpieces = HashMap.newHashMap(16);
        Map<ChessPosition, ChessPiece> cpieces = (isByTeam) ? getcPieces(teamColor) : pieces;
        for (Map.Entry<ChessPosition, ChessPiece> entry : cpieces.entrySet()) {
            if (entry.getValue().getPieceType() == type)
                tcpieces.put(entry.getKey(), entry.getValue());
        }
        return tcpieces;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            addPiece( new ChessPosition(2, i+1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN) );
            board[2][i] = null;
            board[3][i] = null;
            board[4][i] = null;
            board[5][i] = null;
            addPiece( new ChessPosition(7, i+1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN) );
        }
        resetBackRow(board, ChessGame.TeamColor.WHITE, 1);
        resetBackRow(board, ChessGame.TeamColor.BLACK, 8);
    }

    private void resetBackRow(ChessPiece[][] board, ChessGame.TeamColor color, int row) {
        addPiece( new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK) );
        addPiece( new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT) );
        addPiece( new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP) );
        addPiece( new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN) );
        addPiece( new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING) );
        addPiece( new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP) );
        addPiece( new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT) );
        addPiece( new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK) );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("ChessBoard{ \n");
        for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
                if ( board[i][j] != null ) {
                    returnString.append("|").append(board[i][j].shortToString());
                } else {
                    returnString.append("|  ");
                }
            }
            returnString.append("|");
            if (i != 7) returnString.append("\n");
        }
        returnString.append(" }\n");
        return returnString.toString();
    }
}

