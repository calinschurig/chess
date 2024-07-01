package chess;

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
        resetBoard();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
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
        return board[position.getRow()][position.getColumn()];
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int i = 1; i <= 8; i++) {
            board[2][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[7][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        resetBackRow(board, ChessGame.TeamColor.WHITE, 1);
        resetBackRow(board, ChessGame.TeamColor.BLACK, 8);
    }

    private void resetBackRow(ChessPiece[][] board, ChessGame.TeamColor color, int row) {
        board[row][1] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        board[row][2] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row][3] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row][4] = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        board[row][5] = new ChessPiece(color, ChessPiece.PieceType.KING);
        board[row][6] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        board[row][7] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        board[row][8] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
    }
}

