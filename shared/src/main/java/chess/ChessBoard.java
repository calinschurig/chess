package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
        // throw new RuntimeException("Not implemented");
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
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[2][i] = null;
            board[3][i] = null;
            board[4][i] = null;
            board[5][i] = null;
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);

        }
        resetBackRow(board, ChessGame.TeamColor.WHITE, 1);
        resetBackRow(board, ChessGame.TeamColor.BLACK, 8);
    }

    private void resetBackRow(ChessPiece[][] board, ChessGame.TeamColor color, int row) {
        board[row-1][0] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        board[row-1][1] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row-1][2] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row-1][3] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        board[row-1][4] = new ChessPiece(color, ChessPiece.PieceType.KING);
        board[row-1][5] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row-1][6] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row-1][7] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
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
}

