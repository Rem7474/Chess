package model;

import java.util.ArrayList;
import java.util.List;

public class PiecePersonnalisee extends Piece {
    private String name;
    private int unicode;
    private String imagePath;
    private String type; // "pawn", "rook", ...
    private boolean isKing;
    private List<int[]> movePattern;

    public PiecePersonnalisee(String name, int unicode, String imagePath, int row, int col, boolean isWhite, String type, boolean isKing, List<int[]> movePattern) {
        super(row, col, isWhite);
        this.name = name;
        this.unicode = unicode;
        this.imagePath = imagePath;
        this.type = type;
        this.isKing = isKing;
        this.movePattern = movePattern;
    }

    @Override
    public List<int[]> calculatePossibleMoves(int[][] board) {
        List<int[]> possibleMoves = new ArrayList<>();
        if ("pawn".equalsIgnoreCase(type)) {
            int direction = isWhite ? 1 : -1;
            int newRow = row + direction;
            // Avancer d'une case
            if (newRow >= 0 && newRow < 8 && board[newRow][col] == 0) {
                possibleMoves.add(new int[]{newRow, col});
                // Avancer de deux cases depuis la position initiale
                if ((isWhite && row == 1) || (!isWhite && row == 6)) {
                    int twoRow = row + 2 * direction;
                    if (twoRow >= 0 && twoRow < 8 && board[twoRow][col] == 0 && board[newRow][col] == 0) {
                        possibleMoves.add(new int[]{twoRow, col});
                    }
                }
            }
            // Prise en diagonale
            int[] diagCols = {col - 1, col + 1};
            for (int dCol : diagCols) {
                if (dCol >= 0 && dCol < 8 && newRow >= 0 && newRow < 8 && board[newRow][dCol] != 0 && board[newRow][dCol] != (isWhite ? 1 : -1)) {
                    possibleMoves.add(new int[]{newRow, dCol});
                }
            }
            return possibleMoves;
        }
        // Utilise movePattern comme [dL, dC, maxDist]
        for (int[] pattern : movePattern) {
            int dL = pattern[0];
            int dC = pattern[1];
            int maxDist = pattern.length > 2 ? pattern[2] : 1;
            int bounded = (pattern.length > 3) ? pattern[3] : 1; // 1 par défaut (borné)
            int effDL = (isWhite || "pawn".equalsIgnoreCase(type) || dL == 0) ? dL : -dL;
            int effDC = dC;
            for (int step = 1; step <= maxDist; step++) {
                int newRow = row + effDL * step;
                int newCol = col + effDC * step;
                if (newRow < 0 || newRow >= 8 || newCol < 0 || newCol >= 8) break;
                if (board[newRow][newCol] == 0) {
                    possibleMoves.add(new int[]{newRow, newCol});
                } else {
                    if (board[newRow][newCol] != (isWhite ? 1 : -1)) {
                        possibleMoves.add(new int[]{newRow, newCol});
                    }
                    if (bounded == 1) break; // borné : s'arrête sur obstacle
                    // non borné : continue après obstacle
                }
            }
        }
        return possibleMoves;
    }

    public String getName() { return name; }
    public int getUnicode() { return unicode; }
    public String getImagePath() { return imagePath; }
    public String getType() { return type; }
    public boolean isKing() { return isKing; }
    public List<int[]> getMovePattern() { return movePattern; }
}
