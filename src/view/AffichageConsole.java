package view;

import model.Echiquier;

public class AffichageConsole implements Affichage {
    @Override
    public void afficher(Echiquier echiquier) {
        System.out.println("  A B C D E F G H");
        for (int i = 7; i >= 0; i--) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < 8; j++) {
                System.out.print(echiquier.showCase(i, j) + " ");
            }
            System.out.println((i + 1));
        }
        System.out.println("  A B C D E F G H");
    }
}
