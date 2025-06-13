package model;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    protected int row;
    protected int col;
    protected boolean isWhite;

    public Piece(int row, int col, boolean isWhite) {
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
    }

    public abstract List<int[]> calculatePossibleMoves(int[][] board);

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isWhite() {
        return isWhite;
    }

    // Ajoutez des méthodes setter pour row et col
    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
