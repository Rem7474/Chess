package view;

import model.Echiquier;

public class AffichageEchiquier {
    private Echiquier echiquier;

    public AffichageEchiquier(Echiquier echiquier) {
        this.echiquier = echiquier;
    }

    public void afficher() {
        echiquier.afficher();
    }
}
