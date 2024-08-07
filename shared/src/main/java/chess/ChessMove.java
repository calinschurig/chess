package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition start;
    ChessPosition end;
    ChessPiece.PieceType promotion;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition) {
        this(startPosition, endPosition, null, false, false);
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        ChessMove.this.start = startPosition;
        ChessMove.this.end = endPosition;
        ChessMove.this.promotion = promotionPiece;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece, boolean isCastle, boolean isEnPassant) {
        ChessMove.this.start = startPosition;
        ChessMove.this.end = endPosition;
//        ChessMove.this.type = moveType;
        ChessMove.this.promotion = promotionPiece;
    }

    public enum MoveType {
        EMPTY,
        CAPTURE,
        GUARD,
        INVALID,
        UNDETERMINED
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
//        throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
        // throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotion;
//        throw new RuntimeException("Not implemented");
    }

    public MoveType getMoveType (ChessBoard board) {
        ChessPosition myPosition = getStartPosition();
        ChessPosition newPosition = getEndPosition();
        if ( end.getRow() > 8 || end.getRow() < 1  || start.getRow() > 8 || start.getRow() < 1) {
            return MoveType.INVALID;
        }
        if ( end.getColumn() > 8 || end.getColumn() < 1 || start.getColumn() > 8 || start.getColumn() < 1) {
            return MoveType.INVALID;
        }
        if ( board.getPiece(end) == null ) {
            return MoveType.EMPTY;
        }

        if (board.getPiece(start) == null) {
            System.out.println("error: getMoveType start piece is null: " + start);
        }
        if ( start.equals(end) ) {
            return MoveType.INVALID;
        }
        if ( board.getPiece(end).getTeamColor() != board.getPiece(start).getTeamColor() ) {
            return MoveType.CAPTURE;
        } else {
            return MoveType.GUARD;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }

    @Override
    public String toString() {
        return "CM{" +
                "p=" + promotion +
                ", " + start.toString() +
                " -> " + end.toString() +
                '}';
    }
}
