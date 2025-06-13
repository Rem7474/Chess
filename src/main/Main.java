package main;

import controller.Jeu;
import view.AffichageConsole;
import view.AffichageJavaFX;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Choisissez le mode :");
        System.out.println("1. Console");
        System.out.println("2. Démo automatique");
        System.out.println("3. JavaFX (graphique)");
        Scanner scanner = new Scanner(System.in);
        int choix = scanner.nextInt();
        scanner.nextLine();
        if (choix == 1) {
            new Jeu(new AffichageConsole()).demarrerPartie();
        } else if (choix == 2) {
            lancerDemo();
        } else if (choix == 3) {
            new AffichageJavaFX().afficher(null); // À adapter selon ta logique
        }
    }

    private static void lancerDemo() {
        Jeu jeu = new Jeu(new AffichageConsole());
        // Exemple de coups (à adapter)
        String[] coups = {"E2 E4", "E7 E5", "F1 C4", "B8 C6", "D1 H5", "G8 F6", "H5 F7"};
        for (String coup : coups) {
            jeu.affichage.afficher(jeu.echiquier);
            System.out.println("Démo : " + coup);
            String[] parts = coup.split(" ");
            int[] from = jeu.coordToIndex(parts[0]);
            int[] to = jeu.coordToIndex(parts[1]);
            jeu.deplacerPiece(from[0], from[1], to[0], to[1]);
            jeu.isWhiteTurn = !jeu.isWhiteTurn;
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        jeu.affichage.afficher(jeu.echiquier);
        System.out.println("Démo terminée.");
    }
}
