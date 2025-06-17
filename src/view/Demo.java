/*
 * Classe Demo
 * --------------
 * Cette classe permet de lancer une démonstration automatique d'une partie d'échecs avec des pièces personnalisées.
 * Elle fonctionne initialement avec l'interface graphique JavaFX (AffichageJavaFX.JavaFXApp) : elle joue des coups aléatoires,
 * surligne les déplacements, et affiche le message de fin de partie dans l'UI.
 * 
 * Adaptation : une méthode statique startDemoConsole permet désormais de lancer la même démo en mode console,
 * en affichant le plateau à chaque coup via AffichageConsole, sans interface graphique.
 */

package view;

import javafx.application.Platform;
import model.PiecePersonnalisee;
import model.Echiquier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Demo {
    private final AffichageJavaFX.JavaFXApp app;
    private final PiecePersonnalisee[][] pieces;
    private boolean running = false;
    private final Random random = new Random();

    public Demo(AffichageJavaFX.JavaFXApp app, Echiquier echiquier, PiecePersonnalisee[][] pieces) {
        this.app = app;
        this.pieces = pieces;
    }

    public void startDemo() {
        running = true;
        nextMove();
    }

    public void pauseDemo() {
        running = false;
    }
    public void resumeDemo() {
        running = true;
        nextMove();
    }
    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean r) {
        running = r;
    }

    private void nextMove() {
        if (!running) return;
        if (isGameOver()) {
            running = false;
            // Affichage du message de fin de partie dans l'UI
            Platform.runLater(() -> {
                boolean whiteKing = false, blackKing = false;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        PiecePersonnalisee p = pieces[i][j];
                        if (p != null && p.isKing()) {
                            if (p.isWhite()) whiteKing = true;
                            else blackKing = true;
                        }
                    }
                }
                if (!whiteKing)
                    app.showEndGame("Victoire des noirs !");
                else if (!blackKing)
                    app.showEndGame("Victoire des blancs !");
                else
                    app.showEndGame("Fin de partie");
            });
            return;
        }
        // Cherche tous les coups possibles pour le joueur courant
        List<int[]> moves = getAllPossibleMoves(app.isWhiteTurn());
        if (moves.isEmpty()) {
            // Si la partie n'est pas finie mais aucun coup possible, passe le tour et relance la démo
            if (!isGameOver()) {
                Platform.runLater(() -> {
                    app.showTemporaryMessage("Aucune pièce ne peut bouger pour ce joueur. Tour passé automatiquement.", 5000);
                    app.switchPlayer();
                });
                Platform.runLater(this::nextMove); // Relance la démo pour l'autre joueur
            } else {
                running = false;
            }
            return;
        }
        // Joue un coup au hasard
        int[] move = moves.get(random.nextInt(moves.size()));
        int fromRow = move[0], fromCol = move[1], toRow = move[2], toCol = move[3];
        // Surligne la case de départ (highlight jaune) et la case de destination (highlight orange)
        Platform.runLater(() -> {
            app.highlightSelectedCell(fromRow, fromCol, 60);
            app.highlightDestinationCell(toRow, toCol, 60);
        });
        // 1. Surligne les coups possibles (classique) et la case de départ
        Platform.runLater(() -> app.highlightPossibleMoves(app.getPieceAt(fromRow, fromCol), 60));
        // 2. Attend 300ms, puis surligne la case de destination (sans bordure)
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> app.highlightDestinationCellSansBorder(toRow, toCol, 60));
            // 3. Attend 400ms, puis effectue le déplacement
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> app.simulateMove(fromRow, fromCol, toRow, toCol));
            // 4. Attend encore 200ms avant le prochain coup
            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            Platform.runLater(this::nextMove);
        }).start();
    }

    private boolean isGameOver() {
        boolean whiteKing = false, blackKing = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p != null) {
                    if (p.isKing() && p.isWhite()) {
                        whiteKing = true;
                    }
                    if (p.isKing() && !p.isWhite()) {
                        blackKing = true;
                    }
                }
            }
        }
        return !(whiteKing && blackKing);
    }

    private List<int[]> getAllPossibleMoves(boolean white) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p != null && p.isWhite() == white) {
                    List<int[]> possible = p.calculatePossibleMoves(view.BoardUtils.getBoardState(pieces));
                    for (int[] dest : possible) {
                        PiecePersonnalisee destPiece = pieces[dest[0]][dest[1]];
                        if (destPiece == null || destPiece.isWhite() != white) {
                            moves.add(new int[]{i, j, dest[0], dest[1]});
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Lance une démo automatique en mode console (sans interface graphique).
     * Les coups sont joués aléatoirement, le plateau est affiché à chaque tour.
     */
    public static void startDemoConsole(PiecePersonnalisee[][] pieces, int maxCoups) {
        boolean running = true;
        boolean whiteTurn = true;
        int coups = 0;
        while (running && coups < maxCoups) {
            // Affiche le plateau courant
            System.out.println("\nCoup " + (coups+1) + " - Joueur " + (whiteTurn ? "Blancs" : "Noirs"));
            afficherPlateauConsole(pieces);
            // Cherche tous les coups possibles
            List<int[]> moves = getAllPossibleMovesConsole(pieces, whiteTurn);
            if (moves.isEmpty()) {
                System.out.println("Aucun coup possible pour ce joueur. Tour passé.");
                whiteTurn = !whiteTurn;
                coups++;
                continue;
            }
            int[] move = moves.get(new Random().nextInt(moves.size()));
            int fromRow = move[0], fromCol = move[1], toRow = move[2], toCol = move[3];
            // Effectue le déplacement
            PiecePersonnalisee p = pieces[fromRow][fromCol];
            pieces[toRow][toCol] = p;
            pieces[fromRow][fromCol] = null;
            if (p != null) { p.setRow(toRow); p.setCol(toCol); }
            System.out.println("Déplacement: " + p.getName() + " de " + (char)('A'+fromCol) + (fromRow+1) + " à " + (char)('A'+toCol) + (toRow+1));
            // Vérifie la fin de partie
            if (isGameOverConsole(pieces)) {
                running = false;
                System.out.println("Fin de partie! Vainqueur: " + getWinnerConsole(pieces));
            }
            whiteTurn = !whiteTurn;
            coups++;
        }
    }

    /**
     * Affiche le plateau courant (mode console) à partir du tableau de pièces personnalisées.
     */
    private static void afficherPlateauConsole(PiecePersonnalisee[][] pieces) {
        System.out.println("  A B C D E F G H");
        for (int i = 7; i >= 0; i--) {
            System.out.print((i+1) + "|");
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p == null) {
                    System.out.print(" |");
                } else {
                    System.out.print((p.getUnicode() > 0 ? new String(Character.toChars(p.getUnicode())) : p.getName().charAt(0)) + "|");
                }
            }
            System.out.println();
        }
    }

    // Version console de la détection de fin de partie
    private static boolean isGameOverConsole(PiecePersonnalisee[][] pieces) {
        boolean whiteKing = false, blackKing = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p != null && p.isKing()) {
                    if (p.isWhite()) whiteKing = true;
                    else blackKing = true;
                }
            }
        }
        return !(whiteKing && blackKing);
    }
    // Version console pour déterminer le gagnant
    private static String getWinnerConsole(PiecePersonnalisee[][] pieces) {
        boolean whiteKing = false, blackKing = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p != null && p.isKing()) {
                    if (p.isWhite()) whiteKing = true;
                    else blackKing = true;
                }
            }
        }
        if (!whiteKing) return "Noirs";
        if (!blackKing) return "Blancs";
        return "Aucun";
    }
    // Version console pour générer les coups possibles
    private static List<int[]> getAllPossibleMovesConsole(PiecePersonnalisee[][] pieces, boolean white) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                PiecePersonnalisee p = pieces[i][j];
                if (p != null && p.isWhite() == white) {
                    List<int[]> possible = p.calculatePossibleMoves(view.BoardUtils.getBoardState(pieces));
                    for (int[] dest : possible) {
                        PiecePersonnalisee destPiece = pieces[dest[0]][dest[1]];
                        if (destPiece == null || destPiece.isWhite() != white) {
                            moves.add(new int[]{i, j, dest[0], dest[1]});
                        }
                    }
                }
            }
        }
        return moves;
    }
}
