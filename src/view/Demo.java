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
            return;
        }
        // Cherche tous les coups possibles pour le joueur courant
        List<int[]> moves = getAllPossibleMoves(app.isWhiteTurn());
        if (moves.isEmpty()) {
            running = false;
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
                    List<int[]> possible = p.calculatePossibleMoves(getBoardState());
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

    private int[][] getBoardState() {
        int[][] board = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] != null) {
                    board[i][j] = pieces[i][j].isWhite() ? 1 : -1;
                } else {
                    board[i][j] = 0;
                }
            }
        }
        return board;
    }
}
