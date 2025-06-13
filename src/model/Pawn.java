package model;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
    }

    @Override
    public List<int[]> calculatePossibleMoves(int[][] board) {
        List<int[]> possibleMoves = new ArrayList<>();
        int direction = isWhite ? 1 : -1;

        
        int newRow = row + direction;
        // Capturer en diagonale
        int[] diagonalCols = {col - 1, col + 1};
        for (int dCol : diagonalCols) {
            if (dCol >= 0 && dCol < 8) {
                if (newRow >= 0 && newRow < 8 && board[newRow][dCol] != 0 && board[newRow][dCol] != (isWhite ? 1 : -1)) {
                    possibleMoves.add(new int[]{newRow, dCol});
                }
            }
        }
     // Avancer d'une case
        if (newRow >= 0 && newRow < 8 && board[newRow][col] == 0) {
            possibleMoves.add(new int[]{newRow, col});

            // Avancer de deux cases depuis la position initiale
            if ((isWhite && row == 1) || (!isWhite && row == 6)) {
                newRow = row + 2 * direction;
                if (board[newRow][col] == 0) {
                    possibleMoves.add(new int[]{newRow, col});
                }
            }
        }

        

        return possibleMoves;
    }
}
