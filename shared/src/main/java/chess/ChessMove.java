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
//    ChessMove.moveType type;
    ChessPiece.PieceType promotion;
    boolean isCastle;
    boolean isEnPassant;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, /*ChessMove.moveType moveType, */
                     ChessPiece.PieceType promotionPiece) {
        ChessMove.this.start = startPosition;
        ChessMove.this.end = endPosition;
//        ChessMove.this.type = moveType;
        ChessMove.this.promotion = promotionPiece;
        isCastle = false;
        isEnPassant = false;
    }
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, /*ChessMove.moveType moveType, */
                     ChessPiece.PieceType promotionPiece, boolean isCastle, boolean isEnPassant) {
        ChessMove.this.start = startPosition;
        ChessMove.this.end = endPosition;
//        ChessMove.this.type = moveType;
        ChessMove.this.promotion = promotionPiece;
        ChessMove.this.isCastle = isCastle;
        ChessMove.this.isEnPassant = isEnPassant;
    }

    public enum moveType {
        EMPTY,
        CAPTURE,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(start, chessMove.start) && Objects.equals(end, chessMove.end) && promotion == chessMove.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, promotion);
    }
}
