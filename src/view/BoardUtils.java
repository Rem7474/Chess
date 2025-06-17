package view;

import model.PiecePersonnalisee;

public class BoardUtils {
    /**
     * Génère un tableau d'états (1 = blanc, -1 = noir, 0 = vide) à partir du tableau de pièces.
     */
    public static int[][] getBoardState(PiecePersonnalisee[][] pieces) {
        int[][] board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    board[i][j] = pieces[i][j].isWhite() ? 1 : -1;
                } else {
                    board[i][j] = 0;
                }
            }
        }
        return board;
    }
}
