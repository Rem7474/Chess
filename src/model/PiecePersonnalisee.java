package model;

import java.util.ArrayList;
import java.util.List;

public class PiecePersonnalisee {
    private String name;
    private int unicode;
    private String imagePath;
    private String type; // "pawn", "rook", ...
    private boolean isKing;
    private List<int[]> movePattern;
    private int row;
    private int col;
    private boolean isWhite;

    public PiecePersonnalisee(String name, int unicode, String imagePath, int row, int col, boolean isWhite, String type, boolean isKing, List<int[]> movePattern) {
        this.name = name;
        this.unicode = unicode;
        this.imagePath = imagePath;
        this.type = type;
        this.isKing = isKing;
        this.movePattern = movePattern;
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
    }

    public List<int[]> calculatePossibleMoves(int[][] board) {
        List<int[]> possibleMoves = new ArrayList<>();
        // Suppression du flag debug inutile
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
        // Correction : appliquer TOUS les patterns du movePattern
        for (int i = 0; i < movePattern.size(); i++) {
            int[] pattern = movePattern.get(i);
            int dL = pattern[0];
            int dC = pattern[1];
            int maxDist = pattern.length > 2 ? pattern[2] : 1;
            int bounded = (pattern.length > 3) ? pattern[3] : 1; // 1 par défaut (borné)
            if ("knight".equalsIgnoreCase(type)) {
                int newRow = row + dL;
                int newCol = col + dC;
                boolean inBoard = (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8);
                boolean valid = false;
                if (inBoard) {
                    valid = (board[newRow][newCol] == 0 || board[newRow][newCol] != (isWhite ? 1 : -1));
                }
                // Suppression du log debug knight
                if (valid) {
                    possibleMoves.add(new int[]{newRow, newCol});
                }
                continue;
            }
            // Pour les autres pièces (hors pion/cavalier), inverser dL pour les noirs
            int effDL = dL;
            int effDC = dC;
            if (!isWhite && !"pawn".equalsIgnoreCase(type)) {
                effDL = -dL;
            }
            // Suppression du log debug pattern
            for (int step = 1; step <= maxDist; step++) {
                int newRow = row + effDL * step;
                int newCol = col + effDC * step;
                boolean inBoard = (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8);
                boolean valid = false;
                if (inBoard) {
                    if (board[newRow][newCol] == 0) {
                        valid = true;
                    } else if (board[newRow][newCol] != (isWhite ? 1 : -1)) {
                        valid = true;
                    }
                }
                // Suppression du log debug pattern
                if (!inBoard) break;
                if (valid) {
                    possibleMoves.add(new int[]{newRow, newCol});
                }
                if (inBoard && board[newRow][newCol] != 0 && bounded == 1) break;
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
    public boolean isWhite() { return isWhite; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }
}
