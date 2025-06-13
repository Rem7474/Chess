package model;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {

    public Bishop(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> calculatePossibleMoves(int[][] board) {
        List<int[]> possibleMoves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] dir : directions) {
            for (int step = 1; step < 8; step++) {
                int newRow = row + step * dir[0];
                int newCol = col + step * dir[1];

                if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                    if (board[newRow][newCol] == 0) {
                        possibleMoves.add(new int[]{newRow, newCol});
                    } else {
                        if (board[newRow][newCol] != (isWhite ? 1 : -1)) {
                            possibleMoves.add(new int[]{newRow, newCol});
                        }
                        break;
                    }
                } else {
                    break;
                }
            }
        }
        return possibleMoves;
    }
}
