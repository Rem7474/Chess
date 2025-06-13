package main;

import controller.Jeu;
import view.AffichageConsole;
import view.AffichageJavaFX;

public class Main {
    public static void main(String[] args) {
        // Choisissez l'affichage ici
        boolean useJavaFX = true; // Changez à false pour utiliser la console

        Jeu jeu;
        if (useJavaFX) {
            jeu = new Jeu(new AffichageJavaFX());
        } else {
            jeu = new Jeu(new AffichageConsole());
        }

        jeu.demarrerPartie();
    }
}
