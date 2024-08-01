package chess;

import java.util.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] boardArray;
    HashMap<ChessPosition, ChessPiece> piecesMap;

    public ChessBoard() {
        boardArray = new ChessPiece[8][8];
        piecesMap = HashMap.newHashMap(50);
    }

    public static ChessBoard copy(ChessBoard another) {
        ChessBoard newBoard = new ChessBoard();
        for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
            ChessPosition pos = new ChessPosition(i+1, j+1);
            if (another.boardArray[i][j] == null) {
                newBoard.boardArray[i][j] = null;
            }
            else {
                newBoard.addPiece(pos, new ChessPiece( another.boardArray[i][j] ) );
            }
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
        boardArray[position.getRow()-1][position.getColumn()-1] = piece;
        if (piece != null) {
            piecesMap.put(position, piece);
        }
    }
    public ChessPiece removePiece(ChessPosition position) {
        ChessPiece piece = getPiece(position);
        boardArray[position.getRow()-1][position.getColumn()-1] = null;
        piecesMap.remove(position);
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
        return boardArray[position.getRow()-1][position.getColumn()-1];
    }
    public ChessPiece getPieceAt(int row, int col) {
        return boardArray[row-1][col-1];
    }

    public Collection<ChessMove> getPieceMoves(ChessPosition startPosition) {
        if ( getPiece(startPosition) == null ) {
            return new ArrayList<ChessMove>();
        }
        return getPiece(startPosition).pieceMoves(this, startPosition);
    }

    public ChessGame.TeamColor getPieceColor(ChessPosition startPosition) {
        return getPiece(startPosition).getTeamColor();
    }

    public Map<ChessPosition, ChessPiece> getcPieces(ChessGame.TeamColor team) {
        HashMap<ChessPosition, ChessPiece> cpieces = HashMap.newHashMap(16);
        for (Map.Entry<ChessPosition, ChessPiece> entry : piecesMap.entrySet()) {
            if (entry.getValue().getTeamColor() == team) {
                cpieces.put(entry.getKey(), entry.getValue());
            }
        }
        return cpieces;
    }
    public Map<ChessPosition, ChessPiece> getcPiecesOfType(ChessPiece.PieceType type, ChessGame.TeamColor teamColor) {
        return getcPiecesOfType(type, true, teamColor);
    }
    private Map<ChessPosition, ChessPiece> getcPiecesOfType(ChessPiece.PieceType type, boolean isByTeam, ChessGame.TeamColor teamColor) {
        HashMap<ChessPosition, ChessPiece> tcpieces = HashMap.newHashMap(16);
        Map<ChessPosition, ChessPiece> cpieces = (isByTeam) ? getcPieces(teamColor) : piecesMap;
        for (Map.Entry<ChessPosition, ChessPiece> entry : cpieces.entrySet()) {
            if (entry.getValue().getPieceType() == type) {
                tcpieces.put(entry.getKey(), entry.getValue());
            }
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
            boardArray[2][i] = null;
            boardArray[3][i] = null;
            boardArray[4][i] = null;
            boardArray[5][i] = null;
            addPiece( new ChessPosition(7, i+1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN) );
        }
        resetBackRow(boardArray, ChessGame.TeamColor.WHITE, 1);
        resetBackRow(boardArray, ChessGame.TeamColor.BLACK, 8);
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(boardArray, that.boardArray);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardArray);
    }

    @Override
    public String toString() {
        StringBuilder returnString = new StringBuilder("ChessBoard{ \n");
        for (int i = 0; i < 8; i++) { for (int j = 0; j < 8; j++) {
                if ( boardArray[i][j] != null ) {
                    returnString.append("|").append(boardArray[i][j].shortToString());
                } else {
                    returnString.append("|  ");
                }
            }
            returnString.append("|");
            if (i != 7) {
                returnString.append("\n");
            }
        }
        returnString.append(" }\n");
        return returnString.toString();
    }
}

