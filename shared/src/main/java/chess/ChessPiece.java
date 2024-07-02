package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
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
            case KNIGHT -> {
                return knightMoves(board, myPosition);
            }
            case null, default -> {
                return null;
                // throw new RuntimeException("Not implemented");
            }
        }
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                // if (i == 0 && j == 0) break;
                ChessPosition newPosition = new ChessPosition(row+i,col+j);
                // System.out.println("i: " + i + "\tj: " + j + "\tisValid: " + isValidMove(board, myPosition, newPosition) + "\tpiece: " + board.getPiece(newPosition));
                ChessMove.moveType moveType = getMoveType(board, myPosition, newPosition);
                switch (moveType) {
                    case EMPTY , CAPTURE -> {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i+=1) {
            for (int j = -1; j <= 1; j+=1) {
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+(i*distance),col+(j*distance));
                    ChessMove.moveType moveType = getMoveType(board, myPosition, newPosition);
                    boolean shouldBreak = false;
                    switch (moveType) {
                        case EMPTY -> {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case CAPTURE -> {
                            shouldBreak = true;
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case null, default -> {
                            shouldBreak = true;
                        }
                    }
                    if (shouldBreak) break;
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+((i+j)/2*distance),col+((i-j)/2*distance));
                    ChessMove.moveType moveType = getMoveType(board, myPosition, newPosition);
                    boolean shouldBreak = false;
                    switch (moveType) {
                        case EMPTY -> {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case CAPTURE -> {
                            shouldBreak = true;
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case null, default -> {
                            shouldBreak = true;
                        }
                    }
                    if (shouldBreak) break;
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        for (int i = -1; i <= 1; i+=2) {
            for (int j = -1; j <= 1; j+=2) {
                if (i != j && i != -j) break;
                for (int distance = 1; distance < 8; distance++) {
                    ChessPosition newPosition = new ChessPosition(row+(i*distance),col+(j*distance));
                    ChessMove.moveType moveType = getMoveType(board, myPosition, newPosition);
                    boolean shouldBreak = false;
                    switch (moveType) {
                        case EMPTY -> {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case CAPTURE -> {
                            shouldBreak = true;
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                        case null, default -> {
                            shouldBreak = true;
                        }
                    }
                    if (shouldBreak) break;
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int x = 1;
        int y = 2;
        for (int i = 0; i < 8; i++) {
            switch (i) {
                case 0 -> { x=1;  y=2; }
                case 1 -> { x=1;  y=-2; }
                case 2 -> { x=-1; y=2; }
                case 3 -> { x=-1; y=-2; }
                case 4 -> { x=2;  y=1; }
                case 5 -> { x=2;  y=-1; }
                case 6 -> { x=-2; y=1; }
                case 7 -> { x=-2; y=-1; }
            }
            ChessPosition newPosition = new ChessPosition(row+x, col+y);
            switch (getMoveType(board, myPosition, newPosition)) {
                case EMPTY, CAPTURE -> {
                    ChessMove move = new ChessMove(myPosition, newPosition, null);
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }

    private ChessMove.moveType getMoveType (ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if ( newPosition.getRow() > 8 || newPosition.getRow() < 1 ) return ChessMove.moveType.INVALID;
        if ( newPosition.getColumn() > 8 || newPosition.getColumn() < 1 ) return ChessMove.moveType.INVALID;
        if ( board.getPiece(newPosition) == null ) return ChessMove.moveType.EMPTY;

        if ( board.getPiece(newPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor() ) return ChessMove.moveType.CAPTURE;
        else return ChessMove.moveType.INVALID;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
