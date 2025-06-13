package controller;

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
        // Logique pour démarrer et gérer la partie
    }
}
