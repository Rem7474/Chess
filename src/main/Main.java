package main;

import controller.Jeu;
import view.AffichageConsole;
import view.AffichageJavaFX;
import view.Demo;
import model.PieceLoader;
import model.PiecePersonnalisee;

public class Main {
    public static void main(String[] args) {
        // Choisissez l'affichage ici
        boolean useJavaFX = true; // Changez à false pour utiliser la console
        boolean demoConsole = false; // Passez à true pour lancer la démo automatique en console

        if (demoConsole) {
            // Démo automatique en mode console (sans interaction)
            PiecePersonnalisee[][] pieces = new PiecePersonnalisee[8][8];
            for (PiecePersonnalisee p : PieceLoader.chargerDepuisJson("src/pieces.json")) {
                if (p.getRow() >= 0 && p.getRow() < 8 && p.getCol() >= 0 && p.getCol() < 8) {
                    pieces[p.getRow()][p.getCol()] = p;
                }
            }
            Demo.startDemoConsole(pieces, 200); // 200 coups max
            return;
        }

        Jeu jeu;
        if (useJavaFX) {
            jeu = new Jeu(new AffichageJavaFX());
        } else {
            jeu = new Jeu(new AffichageConsole());
        }

        jeu.demarrerPartie();
    }
}
