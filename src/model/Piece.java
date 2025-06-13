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

    public boolean isMoveValid(int toRow, int toCol, Echiquier echiquier) {
        int[][] board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = echiquier.getPiece(i, j);
                if (p == null) board[i][j] = 0;
                else board[i][j] = p.isWhite() ? 1 : -1;
            }
        }
        for (int[] move : this.calculatePossibleMoves(board)) {
            if (move[0] == toRow && move[1] == toCol) return true;
        }
        return false;
    }
}
