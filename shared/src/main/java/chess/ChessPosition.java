package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int row;
    int col;

    public ChessPosition(int row, int col) {
    ChessPosition.this.row = row;
    ChessPosition.this.col = col;
    }
    public ChessPosition(String input) {
        row = Integer.parseInt(input.substring(0, 2));
        col = Integer.parseInt(input.substring(2, 4));
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
        //throw new RuntimeException("Not implemented");
    }

    public ChessPosition rel(int relRow, int relCol) {
        int retRow = row + relRow;
        int retCol = col + relCol;
        if (retRow < 1) {
            retRow = 1;
        }
        if (retRow > 8) {
            retRow = 8;
        }
        if (retCol < 1) {
            retCol = 1;
        }
        if (retCol > 8) {
            retRow = 8;
        }
        return new ChessPosition(retRow, retCol);
    }
    public ChessPosition relUnsafe(int relRow, int relCol) {
        int retRow = row + relRow;
        int retCol = col + relCol;
        return new ChessPosition(retRow, retCol);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }


    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    public String toString() {
        return "(" +
                row +
                "," + col +
                ")";
    }
}
