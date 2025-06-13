package model;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

    public King(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> calculatePossibleMoves(int[][] board) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] move : moves) {
            int newRow = row + move[0];
            int newCol = col + move[1];

            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                if (board[newRow][newCol] == 0 || board[newRow][newCol] != (isWhite ? 1 : -1)) {
                    possibleMoves.add(new int[]{newRow, newCol});
                }
            }
        }
        return possibleMoves;
    }
}
