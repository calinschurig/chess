package chess;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor color;
    PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
    ChessPiece.this.color = pieceColor;
    ChessPiece.this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING -> {
                return kingMoves(board, myPosition);
            }
            case QUEEN -> {
                return queenMoves(board, myPosition);
            }
            case ROOK -> {
                return rookMoves(board, myPosition);
            }
            case BISHOP -> {
                return bishopMoves(board, myPosition);
            }
            case null, default -> {
                return null;
                // throw new RuntimeException("Not implemented");
            }
        }
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = (Collection<ChessMove>) new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) break;

                ChessPosition newPosition = new ChessPosition(row+i,col+j);
                if ( isValidMove(board, myPosition, newPosition) ) {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = (Collection<ChessMove>) new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) break;
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+(i*distance),col+(j*distance));
                    if ( isValidMove(board, myPosition, newPosition) ) {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                    else break;
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = (Collection<ChessMove>) new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == j || i == -j) break;
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+(i*distance),col+(j*distance));
                    if ( isValidMove(board, myPosition, newPosition) ) {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                    else break;
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = (Collection<ChessMove>) new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != j && i != -j) break;
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+(i*distance),col+(j*distance));
                    if ( isValidMove(board, myPosition, newPosition) ) {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                    else break;
                }
            }
        }
        return possibleMoves;
    }

    private boolean isValidMove (ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if ( newPosition.getRow() > 8 || newPosition.getRow() < 1 ) return false;
        if ( newPosition.getColumn() > 8 || newPosition.getColumn() < 1 ) return false;
        if ( board.getPiece(newPosition) == null ) return true;

        if ( board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor() ) return true;
        else return false;
    }

}
