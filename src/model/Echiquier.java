package model;

public class Echiquier {
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

        // Placer toutes les pièces (classiques et personnalisées) depuis le JSON
        try {
            String chemin = "src/pieces.json";
            java.io.File f = new java.io.File(chemin);
            if (f.exists()) {
                java.util.List<PiecePersonnalisee> piecesPerso = PieceLoader.chargerDepuisJson(chemin);
                for (PiecePersonnalisee p : piecesPerso) {
                    if (p.getRow() >= 0 && p.getRow() < 8 && p.getCol() >= 0 && p.getCol() < 8) {
                        plateau[p.getRow()][p.getCol()] = p.getUnicode();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement pièces personnalisées : " + e.getMessage());
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
}
