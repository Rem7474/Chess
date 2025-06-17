package controller;

import java.util.List;
import java.util.Scanner;
import model.Echiquier;
import model.PiecePersonnalisee;
import view.Affichage;

public class Jeu {
    private Echiquier echiquier;
    private Affichage affichage;

    public Jeu(Affichage affichage) {
        echiquier = new Echiquier();
        this.affichage = affichage;
    }

    public void demarrerPartie() {
        affichage.afficher(echiquier);
        if (affichage instanceof view.AffichageConsole) {
            boucleJeuConsole();
        }
        // Pour JavaFX, la logique est déjà gérée dans l'UI
    }

    private void boucleJeuConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean blancs = true;
        while (true) {
            System.out.println("C'est au tour des " + (blancs ? "blancs" : "noirs"));
            int[] depart = demanderCoordonnees(scanner, "Départ (ex: E2)");
            int[] arrivee = demanderCoordonnees(scanner, "Arrivée (ex: E4)");
            int piece = echiquier.getEmplacement(depart[0], depart[1]);
            if (piece == 0) {
                System.out.println("Aucune pièce sur la case de départ.");
                continue;
            }
            PiecePersonnalisee pieceObj = null;
            for (PiecePersonnalisee p : model.PieceLoader.chargerDepuisJson("src/pieces.json")) {
                if (p.getUnicode() == piece && p.getRow() == depart[0] && p.getCol() == depart[1]) {
                    pieceObj = p;
                    break;
                }
            }
            if (pieceObj == null) {
                System.out.println("Erreur : pièce non trouvée dans la configuration JSON.");
                continue;
            }
            if (pieceObj.isWhite() != blancs) {
                System.out.println("Ce n'est pas à vous de jouer cette pièce.");
                continue;
            }
            // Vérification du mouvement via la logique JSON
            List<int[]> coupsPossibles = pieceObj.calculatePossibleMoves(echiquier.getPlateau());
            boolean coupValide = false;
            for (int[] dest : coupsPossibles) {
                if (dest[0] == arrivee[0] && dest[1] == arrivee[1]) {
                    coupValide = true;
                    break;
                }
            }
            if (!coupValide) {
                System.out.println("Mouvement invalide.");
                continue;
            }
            // Effectuer le mouvement
            echiquier.setEmplacement(arrivee[0], arrivee[1], piece);
            echiquier.setEmplacement(depart[0], depart[1], 0);
            affichage.afficher(echiquier);
            // Vérification centralisée de la fin de partie
            String winner = echiquier.getWinner();
            if (winner != null) {
                System.out.println("Fin de partie ! Les " + (winner.equals("white") ? "blancs" : "noirs") + " gagnent.");
                break;
            }
            blancs = !blancs;
        }
    }

    private int[] demanderCoordonnees(Scanner scanner, String message) {
        while (true) {
            System.out.print(message + " : ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.length() == 2) {
                char col = input.charAt(0);
                char row = input.charAt(1);
                if (col >= 'A' && col <= 'H' && row >= '1' && row <= '8') {
                    int colonne = col - 'A';
                    int ligne = row - '1';
                    return new int[]{ligne, colonne};
                }
            }
            System.out.println("Entrée invalide. Format attendu : Lettre(A-H) puis chiffre(1-8), ex: E2");
        }
    }
}
