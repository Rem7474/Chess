package view;

import model.Echiquier;

public class AffichageConsole implements Affichage {
    @Override
    public void afficher(Echiquier echiquier) {
        echiquier.afficher();
    }
}
