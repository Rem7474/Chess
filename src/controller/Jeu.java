package controller;

import model.*;
import view.Affichage;
import java.util.Scanner;

public class Jeu {
    private Echiquier echiquier;
    private Affichage affichage;
    private boolean isWhiteTurn = true;

    public Jeu(Affichage affichage) {
        echiquier = new Echiquier();
        this.affichage = affichage;
    }

    public void demarrerPartie() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            affichage.afficher(echiquier);
            System.out.println((isWhiteTurn ? "Blancs" : "Noirs") + " : Entrez le coup (ex: E2 E4) ou 'exit' pour quitter :");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit")) break;
            String[] parts = input.trim().split("\\s+");
            if (parts.length != 2 || !isCoordonneeValide(parts[0]) || !isCoordonneeValide(parts[1])) {
                System.out.println("Entrée invalide. Format attendu : E2 E4");
                continue;
            }
            int[] from = coordToIndex(parts[0]);
            int[] to = coordToIndex(parts[1]);
            if (!deplacerPiece(from[0], from[1], to[0], to[1])) {
                System.out.println("Coup invalide, réessayez.");
                continue;
            }
            isWhiteTurn = !isWhiteTurn;
        }
        scanner.close();
    }

    private boolean isCoordonneeValide(String coord) {
        return coord.matches("[A-Ha-h][1-8]");
    }

    private int[] coordToIndex(String coord) {
        int col = Character.toUpperCase(coord.charAt(0)) - 'A';
        int row = Character.getNumericValue(coord.charAt(1)) - 1;
        return new int[]{row, col};
    }

    private boolean deplacerPiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = echiquier.getPiece(fromRow, fromCol);
        if (piece == null) return false;
        if (piece.isWhite() != isWhiteTurn) return false;
        if (!piece.isMoveValid(toRow, toCol, echiquier)) return false;
        // Prise éventuelle
        echiquier.setPiece(toRow, toCol, piece);
        echiquier.setPiece(fromRow, fromCol, null);
        piece.setRow(toRow);
        piece.setCol(toCol);
        return true;
    }
}
