package chess;

import java.util.*;
import java.util.function.Function;

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

    public ChessPiece(ChessPiece another) {
        this.color = another.color;
        this.type = another.type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
    public void setPieceType(PieceType typeToSet) {
        type = typeToSet;
    }


    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return pieceMoves(board, myPosition, false);
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        switch (type) {
            case KING -> {
                return kingMoves(board, myPosition, includeGuard);
            }
            case QUEEN -> {
                return queenMoves(board, myPosition, includeGuard);
            }
            case ROOK -> {
                return rookMoves(board, myPosition, includeGuard);
            }
            case BISHOP -> {
                return bishopMoves(board, myPosition, includeGuard);
            }
            case KNIGHT -> {
                return knightMoves(board, myPosition, includeGuard);
            }
            case PAWN -> {
                return pawnMoves(board, myPosition, includeGuard);
            }
            case null, default -> {
                return null;
                // throw new RuntimeException("Not implemented");
            }
        }
    }

    public Collection<ChessMove> myZoneOfControl(ChessBoard board, ChessPosition myPosition) {
        if (type != PieceType.PAWN) {
            return pieceMoves(board, myPosition, true);
        }
        HashSet<ChessMove> possibleMoves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int direction = 1;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        for (int i = -1; i <= 1; i+=2) {
            ChessPosition newPosition = new ChessPosition(row+direction, col+i);
            switch (getMoveType(board, myPosition, newPosition)) {
                case EMPTY, CAPTURE, GUARD -> { possibleMoves.add(new ChessMove(myPosition, newPosition)); }
            }
        }
        return possibleMoves;
    }


    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        return kingMoves(board, myPosition, false);
    }
    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        possibleMoves.addAll( addMoves(
                (rowDir, colDir) -> {
                    if ( rowDir == 1 || rowDir == -1 || colDir == 1 || colDir == -1 ) {
                        return true;
                    } else {
                        return false;
                    }
                },
                (dist) -> {
                    return dist == 1;
                },
                board,
                myPosition,
                includeGuard
        ));
        return possibleMoves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        return queenMoves(board, myPosition, false);
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        possibleMoves.addAll( addMoves(
                (rowDir, colDir) -> {
                    if ( rowDir == 1 || rowDir == -1 || colDir == 1 || colDir == -1 ) {
                        return true;
                    } else {
                        return false;
                    }
                },
                (dist) -> {
                    return dist < 8;
                },
                board,
                myPosition,
                includeGuard
        ));
        return possibleMoves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        return rookMoves(board, myPosition, false);
    }
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        possibleMoves.addAll( addMoves(
                (rowDir, colDir) -> {
                    if ( (rowDir == 0 && colDir != 0) || (colDir == 0 && rowDir != 0) ) {
                        return true;
                    } else {
                        return false;
                    }
                },
                (dist) -> {
                    return dist < 8;
                },
                board,
                myPosition,
                includeGuard
        ));
        return possibleMoves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        return bishopMoves(board, myPosition, false);
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Set<ChessMove> possibleMoves = new java.util.HashSet<>();
        possibleMoves.addAll( addMoves(
                (rowDir, colDir) -> {
                    if ( (rowDir == 1 || rowDir == -1) && (colDir == 1 || colDir == -1) ) {
                        return true;
                    } else {
                        return false;
                    }
                },
                (dist) -> {
                    return dist < 8;
                },
                board,
                myPosition,
                includeGuard
        ));
        return possibleMoves;
    }
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        return knightMoves(board, myPosition, false);
    }
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
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
                case GUARD -> {
                    if (includeGuard) {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                }
            }
        }
        return possibleMoves;
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        return pawnMoves(board, myPosition, false);
    }
    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Collection<ChessMove> possibleMoves = new java.util.ArrayList<>(List.of());
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int defaultRow = 2;
        int direction = 1;
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            defaultRow = 7;
            direction = -1;
        }
        for (int i = -1; i <= 1; i+=1) {
            ChessPosition newPosition = new ChessPosition(row+direction, col+i);
            switch (getMoveType(board, myPosition, newPosition)) {
                case EMPTY -> {
                    if (i != 0) {
                        break;
                    }
                    if (row+direction == 1 || row+direction == 8) {
                        possibleMoves.addAll(promotionMoves(myPosition, newPosition));
                    } else {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                    if (row == defaultRow) {
                        newPosition = new ChessPosition(row + 2*direction, col);
                        if (getMoveType(board, myPosition, newPosition) == ChessMove.MoveType.EMPTY) {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                    }
                }
                case CAPTURE -> {
                    if (i == 0) {
                        break;
                    }
                    if (row+direction == 1 || row+direction == 8) {
                        possibleMoves.addAll(promotionMoves(myPosition, newPosition));
                    } else {
                        ChessMove move = new ChessMove(myPosition, newPosition, null);
                        possibleMoves.add(move);
                    }
                }
                case GUARD -> {
                    if (i == 0) {
                        break;
                    }
                    if (includeGuard) {
                        if (row+direction == 1 || row+direction == 8) {
                            possibleMoves.addAll(promotionMoves(myPosition, newPosition));
                        } else {
                            ChessMove move = new ChessMove(myPosition, newPosition, null);
                            possibleMoves.add(move);
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    private ChessMove.MoveType getMoveType (ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        ChessMove move = new ChessMove(myPosition, newPosition);
        return move.getMoveType(board);
    }

    private Collection<ChessMove> promotionMoves(ChessPosition startPosition, ChessPosition endPosition) {
        Collection<ChessMove> possiblePromotionMoves = new java.util.ArrayList<>(List.of());
        for (int i = 0; i < 4; i++) {
            ChessPiece.PieceType promotion = switch (i) {
                case 0 -> PieceType.QUEEN;
                case 1 -> PieceType.ROOK;
                case 2 -> PieceType.BISHOP;
                case 3 -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            };
            possiblePromotionMoves.add(new ChessMove(startPosition, endPosition, promotion));
        }
        return possiblePromotionMoves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    public String shortToString() {
        String toReturn;
        if (color == ChessGame.TeamColor.WHITE) {
            toReturn = "w";
        }
        else  {
            toReturn = "b";
        }
        switch (type) {
            case KING -> toReturn += "K";
            case QUEEN -> toReturn += "Q";
            case ROOK -> toReturn += "R";
            case BISHOP -> toReturn += "B";
            case KNIGHT -> toReturn += "N";
            case PAWN -> toReturn += "P";
            case null -> toReturn += "null";
        }
        return toReturn;
    }

    private interface BinaryIntToBoolean {
        boolean check(int i, int j);
    }
    Set<ChessMove> addMoves(BinaryIntToBoolean selectDirection, Function<Integer, Boolean> selectDistance,
                            ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Set<ChessMove> moves = new HashSet<>();
        int[] possibleDirs = {-1, 0, 1};
        for (int[] dir : permutations(possibleDirs, possibleDirs)) {
            if (selectDirection.check(dir[0], dir[1])) {
                moves.addAll( addMovesAtDistance(selectDistance, dir, board, myPosition, includeGuard));
            } else {
                continue;
            }

        }
        return moves;
    }

    Set<ChessMove> addMovesAtDistance(Function<Integer, Boolean> selectDistance, int[] direction,
                                      ChessBoard board, ChessPosition myPosition, boolean includeGuard) {
        Set<ChessMove> possibleMoves = new HashSet<>();
        boolean shouldBreak = false;
        for (int dist = 1; dist < 8; dist++) {
            if (!selectDistance.apply(dist)) {
                break;
            }
            ChessMove move = new ChessMove(
                    myPosition,
                    myPosition.relUnsafe(direction[0]*dist, direction[1]*dist)
            );
            ChessMove.MoveType moveType = move.getMoveType(board);
            switch (moveType) {
                case EMPTY -> {
                    possibleMoves.add(move);
                }
                case CAPTURE -> {
                    shouldBreak = true;
                    possibleMoves.add(move);
                }
                case GUARD -> {
                    shouldBreak = true;
                    if (includeGuard) {
                        possibleMoves.add(move);
                    }
                }
                case null, default -> {
                    shouldBreak = true;
                }
            }
            if (shouldBreak) {
                break;
            }
        }
        return possibleMoves;
    }



    Set<int[]> permutations(int[] aList, int[] bList) {
        Set<int[]> result = new HashSet<>();
        for (int a : aList) {
            for (int b : bList) {
                result.add(new int[]{a, b});
            }
        }
        return result;
    }

}
