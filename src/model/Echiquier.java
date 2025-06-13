package model;

public class Echiquier {
    // Constantes pour les pièces
    static public final int ROOK_WHITE = 0x2656;
    static public final int ROOK_BLACK = 0x265C;
    static public final int KNIGHT_WHITE = 0x2658;
    static public final int KNIGHT_BLACK = 0x265E;
    static public final int BISHOP_WHITE = 0x2657;
    static public final int BISHOP_BLACK = 0x265D;
    static public final int PAWN_WHITE = 0x2659;
    static public final int PAWN_BLACK = 0x265F;
    static public final int QUEEN_WHITE = 0x2655;
    static public final int QUEEN_BLACK = 0x265B;
    static public final int KING_WHITE = 0x2654; // Correction pour le roi blanc
    static public final int KING_BLACK = 0x265A;

    // Constantes pour les colonnes
    static public final int A = 0;
    static public final int B = 1;
    static public final int C = 2;
    static public final int D = 3;
    static public final int E = 4;
    static public final int F = 5;
    static public final int G = 6;
    static public final int H = 7;

    private int[][] plateau;

    public Echiquier() {
        plateau = new int[8][8];
        initialize();
    }

    private void initialize() {
        // Initialiser le plateau à 0 (vide)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                plateau[i][j] = 0;
            }
        }

        // Placer les pièces noires
        plateau[7][A] = ROOK_BLACK;
        plateau[7][B] = KNIGHT_BLACK;
        plateau[7][C] = BISHOP_BLACK;
        plateau[7][D] = QUEEN_BLACK;
        plateau[7][E] = KING_BLACK;
        plateau[7][F] = BISHOP_BLACK;
        plateau[7][G] = KNIGHT_BLACK;
        plateau[7][H] = ROOK_BLACK;

        for (int i = 0; i < 8; i++) {
            plateau[6][i] = PAWN_BLACK;
        }

        // Placer les pièces blanches
        plateau[0][A] = ROOK_WHITE;
        plateau[0][B] = KNIGHT_WHITE;
        plateau[0][C] = BISHOP_WHITE;
        plateau[0][D] = QUEEN_WHITE;
        plateau[0][E] = KING_WHITE;
        plateau[0][F] = BISHOP_WHITE;
        plateau[0][G] = KNIGHT_WHITE;
        plateau[0][H] = ROOK_WHITE;

        for (int i = 0; i < 8; i++) {
            plateau[1][i] = PAWN_WHITE;
        }
    }

    public void afficher() {
        String lignePlateau;
        for (int ligne = 7; ligne >= 0; ligne--) {
            lignePlateau = (ligne + 1) + "|";
            for (int colonne = 0; colonne < 8; colonne++) {
                if (plateau[ligne][colonne] == 0) {
                    lignePlateau += " |";
                } else {
                    lignePlateau += show(plateau[ligne][colonne]) + "|";
                }
            }
            System.out.println(lignePlateau);
        }
        System.out.println("  A B C D E F G H");
    }

    public void setEmplacement(int ligne, int colonne, int piece) {
        plateau[ligne][colonne] = piece;
    }

    public int getEmplacement(int ligne, int colonne) {
        return plateau[ligne][colonne];
    }

    public String show(int piece) {
        return new String(Character.toChars(piece));
    }

    public static String intToColonneName(int colonne) {
        return new String(new char[]{(char) ('A' + colonne)});
    }

    public void mouvementPion(boolean debut, int ligne, int colonne) {
        // Logique pour le mouvement du pion
        String caseLibre = "";
        if (getEmplacement(ligne + 1, colonne) == 0) {
            String nomColonne = intToColonneName(colonne);
            caseLibre = nomColonne + (ligne + 1);
        }

        if (debut) {
            if (getEmplacement(ligne + 2, colonne) == 0) {
                String nomColonne = intToColonneName(colonne);
                caseLibre = nomColonne + (ligne + 2);
            }
        }
    }
}
