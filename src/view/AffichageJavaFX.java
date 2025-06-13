package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import model.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class AffichageJavaFX implements Affichage {

    private static Piece[][] pieces; // Plateau de pièces
    private static boolean isWhiteTurn = true;
    private static int selectedRow = -1;
    private static int selectedCol = -1;
    private static String dernierCoup = "";

    @Override
    public void afficher(Echiquier echiquier) {
        pieces = new Piece[8][8];
        initializePieces();
        JavaFXApp.launchJavaFX();
    }

    private void initializePieces() {
        // Pièces blanches
        pieces[0][0] = new Rook(0, 0, true);
        pieces[0][1] = new Knight(0, 1, true);
        pieces[0][2] = new Bishop(0, 2, true);
        pieces[0][3] = new Queen(0, 3, true);
        pieces[0][4] = new King(0, 4, true);
        pieces[0][5] = new Bishop(0, 5, true);
        pieces[0][6] = new Knight(0, 6, true);
        pieces[0][7] = new Rook(0, 7, true);
        for (int i = 0; i < 8; i++) {
            pieces[1][i] = new Pawn(1, i, true);
        }
        // Pièces noires
        pieces[7][0] = new Rook(7, 0, false);
        pieces[7][1] = new Knight(7, 1, false);
        pieces[7][2] = new Bishop(7, 2, false);
        pieces[7][3] = new Queen(7, 3, false);
        pieces[7][4] = new King(7, 4, false);
        pieces[7][5] = new Bishop(7, 5, false);
        pieces[7][6] = new Knight(7, 6, false);
        pieces[7][7] = new Rook(7, 7, false);
        for (int i = 0; i < 8; i++) {
            pieces[6][i] = new Pawn(6, i, false);
        }
        // Cases vides
        for (int i = 2; i < 6; i++)
            for (int j = 0; j < 8; j++)
                pieces[i][j] = null;
    }

    public static class JavaFXApp extends Application {
        private static Stage mainStage;
        private static Label joueurLabel;
        private static Label infoLabel;
        private static Label dernierCoupLabel;
        private static GridPane gridPane;
        private static double cellSize = 60;

        public static void launchJavaFX() {
            launch();
        }

        @Override
        public void start(Stage primaryStage) {
            mainStage = primaryStage;
            BorderPane root = new BorderPane();

            // Barre de contrôle
            HBox controlBar = new HBox(15);
            controlBar.setAlignment(Pos.CENTER);
            Button startBtn = new Button("Start");
            Button restartBtn = new Button("Restart");
            joueurLabel = new Label("Joueur : Blancs");
            infoLabel = new Label("Bienvenue !");
            dernierCoupLabel = new Label("Dernier coup : ");
            controlBar.getChildren().addAll(startBtn, restartBtn, joueurLabel, infoLabel, dernierCoupLabel);

            // Plateau d'échecs
            gridPane = new GridPane();
            afficherPlateau();

            // Actions des boutons
            startBtn.setOnAction(e -> {
                resetGame();
                infoLabel.setText("Partie commencée !");
            });
            restartBtn.setOnAction(e -> {
                resetGame();
                infoLabel.setText("Partie réinitialisée !");
            });

            root.setTop(controlBar);
            root.setCenter(gridPane);

            Scene scene = new Scene(root, 8 * cellSize + 40, 8 * cellSize + 80);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Jeu d'échecs");
            primaryStage.show();
        }

        private static void afficherPlateau() {
            gridPane.getChildren().clear();
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    StackPane cell = new StackPane();
                    Rectangle rect = new Rectangle(cellSize, cellSize);
                    rect.setFill((row + col) % 2 == 0 ? Color.BEIGE : Color.BROWN);
                    cell.getChildren().add(rect);

                    Piece piece = pieces[row][col];
                    if (piece != null) {
                        Text pieceText = new Text(getUnicode(piece));
                        pieceText.setStyle("-fx-font-size: 32px;");
                        cell.getChildren().add(pieceText);
                    }

                    int r = row, c = col;
                    cell.setOnMouseClicked(e -> handleCellClick(r, c));
                    gridPane.add(cell, col, 7 - row); // Affichage ligne 8 en haut
                }
            }
        }

        private static void handleCellClick(int row, int col) {
            Piece piece = pieces[row][col];
            if (selectedRow == -1 && piece != null && piece.isWhite() == isWhiteTurn) {
                selectedRow = row;
                selectedCol = col;
                infoLabel.setText("Sélectionnez une case de destination.");
            } else if (selectedRow != -1) {
                if (row == selectedRow && col == selectedCol) {
                    selectedRow = -1;
                    selectedCol = -1;
                    infoLabel.setText("Sélection annulée.");
                } else {
                    if (tryMove(selectedRow, selectedCol, row, col)) {
                        dernierCoup = getCoord(selectedRow, selectedCol) + " -> " + getCoord(row, col);
                        dernierCoupLabel.setText("Dernier coup : " + dernierCoup);
                        isWhiteTurn = !isWhiteTurn;
                        joueurLabel.setText("Joueur : " + (isWhiteTurn ? "Blancs" : "Noirs"));
                        infoLabel.setText("Coup joué !");
                    } else {
                        infoLabel.setText("Coup invalide !");
                    }
                    selectedRow = -1;
                    selectedCol = -1;
                }
                afficherPlateau();
            }
        }

        private static boolean tryMove(int fromRow, int fromCol, int toRow, int toCol) {
            Piece piece = pieces[fromRow][fromCol];
            if (piece == null || piece.isWhite() != isWhiteTurn) return false;
            if (piece.isMoveValid(toRow, toCol, pieces)) {
                pieces[toRow][toCol] = piece;
                pieces[fromRow][fromCol] = null;
                piece.setRow(toRow);
                piece.setCol(toCol);
                return true;
            }
            return false;
        }

        private static void resetGame() {
            isWhiteTurn = true;
            selectedRow = -1;
            selectedCol = -1;
            dernierCoup = "";
            joueurLabel.setText("Joueur : Blancs");
            infoLabel.setText("Plateau réinitialisé !");
            dernierCoupLabel.setText("Dernier coup : ");
            // Réinitialise les pièces
            AffichageJavaFX affichage = new AffichageJavaFX();
            affichage.initializePieces();
            afficherPlateau();
        }

        private static String getUnicode(Piece piece) {
            if (piece instanceof Rook) return piece.isWhite() ? "♖" : "♜";
            if (piece instanceof Knight) return piece.isWhite() ? "♘" : "♞";
            if (piece instanceof Bishop) return piece.isWhite() ? "♗" : "♝";
            if (piece instanceof Queen) return piece.isWhite() ? "♕" : "♛";
            if (piece instanceof King) return piece.isWhite() ? "♔" : "♚";
            if (piece instanceof Pawn) return piece.isWhite() ? "♙" : "♟";
            return "?";
        }

        private static String getCoord(int row, int col) {
            char c = (char) ('A' + col);
            int r = row + 1;
            return "" + c + r;
        }
    }
}
