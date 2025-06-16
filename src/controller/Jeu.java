package controller;

import java.util.Scanner;
import model.Echiquier;
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
            boolean pieceBlanche = Character.isUpperCase(echiquier.show(piece).charAt(0)) || piece == Echiquier.KING_WHITE || piece == Echiquier.QUEEN_WHITE || piece == Echiquier.ROOK_WHITE || piece == Echiquier.BISHOP_WHITE || piece == Echiquier.KNIGHT_WHITE || piece == Echiquier.PAWN_WHITE;
            if (pieceBlanche != blancs) {
                System.out.println("Ce n'est pas à vous de jouer cette pièce.");
                continue;
            }
            // Vérification du mouvement (mouvements de base, prise, hors roque/promotion/échec)
            if (!mouvementValide(piece, depart, arrivee, blancs)) {
                System.out.println("Mouvement invalide.");
                continue;
            }
            // Effectuer le mouvement
            int pieceCapturee = echiquier.getEmplacement(arrivee[0], arrivee[1]);
            echiquier.setEmplacement(arrivee[0], arrivee[1], piece);
            echiquier.setEmplacement(depart[0], depart[1], 0);
            affichage.afficher(echiquier);
            // Fin de partie si roi capturé
            if (pieceCapturee == Echiquier.KING_WHITE || pieceCapturee == Echiquier.KING_BLACK) {
                System.out.println("Fin de partie ! Le roi a été capturé. Les " + (blancs ? "blancs" : "noirs") + " gagnent.");
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

    private boolean mouvementValide(int piece, int[] depart, int[] arrivee, boolean blancs) {
        int dL = arrivee[0] - depart[0];
        int dC = arrivee[1] - depart[1];
        int cible = echiquier.getEmplacement(arrivee[0], arrivee[1]);
        // Empêcher de prendre une pièce alliée
        if (cible != 0 && ((blancs && (cible == Echiquier.KING_WHITE || cible == Echiquier.QUEEN_WHITE || cible == Echiquier.ROOK_WHITE || cible == Echiquier.BISHOP_WHITE || cible == Echiquier.KNIGHT_WHITE || cible == Echiquier.PAWN_WHITE)) || (!blancs && (cible == Echiquier.KING_BLACK || cible == Echiquier.QUEEN_BLACK || cible == Echiquier.ROOK_BLACK || cible == Echiquier.BISHOP_BLACK || cible == Echiquier.KNIGHT_BLACK || cible == Echiquier.PAWN_BLACK)))) {
            return false;
        }
        // Règles de déplacement de base (exemple pour pion, à compléter pour autres pièces)
        // Pion
        if ((piece == Echiquier.PAWN_WHITE && blancs) || (piece == Echiquier.PAWN_BLACK && !blancs)) {
            int dir = blancs ? 1 : -1;
            // Avance d'une case
            if (dC == 0 && dL == dir && cible == 0) return true;
            // Avance de deux cases depuis la position initiale
            if (dC == 0 && dL == 2 * dir && cible == 0 && ((blancs && depart[0] == 1) || (!blancs && depart[0] == 6)) && echiquier.getEmplacement(depart[0] + dir, depart[1]) == 0) return true;
            // Prise en diagonale
            if (Math.abs(dC) == 1 && dL == dir && cible != 0 && ((blancs && cible != Echiquier.PAWN_WHITE) || (!blancs && cible != Echiquier.PAWN_BLACK))) return true;
            return false;
        }
        // Tour
        if ((piece == Echiquier.ROOK_WHITE && blancs) || (piece == Echiquier.ROOK_BLACK && !blancs)) {
            if (dL == 0 && dC != 0) return cheminLibre(depart, arrivee);
            if (dC == 0 && dL != 0) return cheminLibre(depart, arrivee);
            return false;
        }
        // Cavalier
        if ((piece == Echiquier.KNIGHT_WHITE && blancs) || (piece == Echiquier.KNIGHT_BLACK && !blancs)) {
            return (Math.abs(dL) == 2 && Math.abs(dC) == 1) || (Math.abs(dL) == 1 && Math.abs(dC) == 2);
        }
        // Fou
        if ((piece == Echiquier.BISHOP_WHITE && blancs) || (piece == Echiquier.BISHOP_BLACK && !blancs)) {
            if (Math.abs(dL) == Math.abs(dC) && dL != 0) return cheminLibre(depart, arrivee);
            return false;
        }
        // Reine
        if ((piece == Echiquier.QUEEN_WHITE && blancs) || (piece == Echiquier.QUEEN_BLACK && !blancs)) {
            if ((dL == 0 && dC != 0) || (dC == 0 && dL != 0) || (Math.abs(dL) == Math.abs(dC) && dL != 0)) return cheminLibre(depart, arrivee);
            return false;
        }
        // Roi
        if ((piece == Echiquier.KING_WHITE && blancs) || (piece == Echiquier.KING_BLACK && !blancs)) {
            return Math.abs(dL) <= 1 && Math.abs(dC) <= 1;
        }
        return false;
    }

    private boolean cheminLibre(int[] depart, int[] arrivee) {
        int dL = Integer.signum(arrivee[0] - depart[0]);
        int dC = Integer.signum(arrivee[1] - depart[1]);
        int l = depart[0] + dL;
        int c = depart[1] + dC;
        while (l != arrivee[0] || c != arrivee[1]) {
            if (echiquier.getEmplacement(l, c) != 0) return false;
            l += dL;
            c += dC;
        }
        return true;
    }
}
