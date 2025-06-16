package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

import java.awt.*;
import java.util.List;

public class AffichageJavaFX implements Affichage {

    private static int selectedPieceRow = -1;
    private static int selectedPieceCol = -1;
    private static boolean isWhiteTurn = true;
    private static Piece[][] pieces;

    @Override
    public void afficher(Echiquier echiquier) {
        pieces = new Piece[8][8];
        initializePieces(echiquier);
        JavaFXApp.echiquier = echiquier;
        Application.launch(JavaFXApp.class);
    }

    private static void initializePieces(Echiquier echiquier) {
        // Initialisation des pièces (exemple, adapte selon ta logique)
        pieces[0][0] = new Rook(0, 0, true);
        pieces[0][7] = new Rook(0, 7, true);
        pieces[0][1] = new Knight(0, 1, true);
        pieces[0][6] = new Knight(0, 6, true);
        pieces[0][2] = new Bishop(0, 2, true);
        pieces[0][5] = new Bishop(0, 5, true);
        pieces[0][3] = new Queen(0, 3, true);
        pieces[0][4] = new King(0, 4, true);
        for (int i = 0; i < 8; i++) {
            pieces[1][i] = new Pawn(1, i, true);
        }
        pieces[7][0] = new Rook(7, 0, false);
        pieces[7][7] = new Rook(7, 7, false);
        pieces[7][1] = new Knight(7, 1, false);
        pieces[7][6] = new Knight(7, 6, false);
        pieces[7][2] = new Bishop(7, 2, false);
        pieces[7][5] = new Bishop(7, 5, false);
        pieces[7][3] = new Queen(7, 3, false);
        pieces[7][4] = new King(7, 4, false);
        for (int i = 0; i < 8; i++) {
            pieces[6][i] = new Pawn(6, i, false);
        }
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
            }
        }
    }

    public static class JavaFXApp extends Application {
        public static Echiquier echiquier;
        private Stage primaryStage;
        private Label turnLabel;
        private Label endGameLabel;
        private GridPane gridPane;
        private boolean gameEnded = false;

        @Override
        public void start(Stage primaryStage) {
            this.primaryStage = primaryStage;
            gridPane = new GridPane();
            double cellSize = 60;
            gridPane.setAlignment(Pos.CENTER); // Correction : centre le contenu du GridPane
            gridPane.setPrefSize(8 * cellSize, 8 * cellSize); // Correction : taille fixe

            afficherPlateau(cellSize);

            turnLabel = new Label(isWhiteTurn ? "Tour : Blancs" : "Tour : Noirs");
            turnLabel.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #222;" +
                "-fx-padding: 8 20 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color: #e0e7ef;"
            );

            endGameLabel = new Label("");
            endGameLabel.setVisible(false);
            endGameLabel.setStyle(
                "-fx-font-size: 32px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #d7263d;" +
                "-fx-background-color: rgba(255,251,230,0.95);" +
                "-fx-padding: 32 48 32 48;" +
                "-fx-border-color: #d7263d;" +
                "-fx-border-width: 3px;" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;"
            );
            StackPane.setAlignment(endGameLabel, Pos.CENTER);

            Button reloadButton = new Button("⟳ Rejouer");
            reloadButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px;" +
                "-fx-padding: 8 24 8 24; " +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            reloadButton.setOnMouseEntered(e -> reloadButton.setStyle(reloadButton.getStyle() + "-fx-cursor: hand;"));
            reloadButton.setOnMouseExited(e -> reloadButton.setStyle(reloadButton.getStyle().replace("-fx-cursor: hand;", "")));
            reloadButton.setOnAction(e -> {
                isWhiteTurn = true;
                selectedPieceRow = -1;
                selectedPieceCol = -1;
                gameEnded = false;
                endGameLabel.setText("");
                endGameLabel.setVisible(false);
                AffichageJavaFX.initializePieces(new Echiquier());
                gridPane.getChildren().clear();
                afficherPlateau(cellSize);
                turnLabel.setText("Tour : Blancs");
                gridPane.setDisable(false);
            });

            Button demoButton = new Button("Démo");
            demoButton.setStyle(
                "-fx-background-color: #2196F3; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px;" +
                "-fx-padding: 8 24 8 24; " +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            demoButton.setOnMouseEntered(ev -> demoButton.setStyle(demoButton.getStyle() + "-fx-cursor: hand;"));
            demoButton.setOnMouseExited(ev -> demoButton.setStyle(demoButton.getStyle().replace("-fx-cursor: hand;", "")));
            demoButton.setOnAction(ev -> {
                Demo demo = new Demo(this, echiquier, pieces);
                demo.startDemo();
            });

            HBox topBar = new HBox(40, turnLabel, reloadButton, demoButton);
            topBar.setStyle("-fx-alignment: center; -fx-padding: 18; -fx-spacing: 40;");

            VBox centerBox = new VBox(20, gridPane, endGameLabel);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setFillWidth(false);

            // Création du conteneur principal en superposition
            StackPane rootStack = new StackPane();

            BorderPane root = new BorderPane();
            root.setTop(topBar);
            root.setCenter(centerBox);
            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f4f8, #dbeafe);");

            rootStack.getChildren().addAll(root, endGameLabel);

            Scene scene = new Scene(rootStack, 700, 800);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Jeu d'échecs");

            // Limite la hauteur de la fenêtre à la hauteur de l'écran moins une marge (ex: 100px)
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double maxHeight = screenSize.getHeight() - 100; // 100px de marge pour la barre Windows

            if (800 > maxHeight) {
                primaryStage.setHeight(maxHeight);
            } else {
                primaryStage.setHeight(800);
            }
            primaryStage.setMinHeight(600); // Optionnel : hauteur minimale

            primaryStage.show();
        }

        private void afficherPlateau(double cellSize) {
            gridPane.getChildren().clear();
            for (int ligne = 7; ligne >= 0; ligne--) {
                for (int colonne = 0; colonne < 8; colonne++) {
                    Piece piece = pieces[ligne][colonne];
                    StackPane cell = new StackPane();
                    cell.setStyle(getCellStyle(ligne, colonne, cellSize));

                    final int currentRow = ligne;
                    final int currentCol = colonne;

                    if (piece != null) {
                        String imagePath = getImagePath(piece);
                        Image image = new Image(getClass().getResourceAsStream("/images/" + imagePath));
                        if (image != null) {
                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(cellSize);
                            imageView.setFitHeight(cellSize);
                            cell.getChildren().add(imageView);
                        }
                        cell.setOnMouseClicked(event -> {
                            if (!gameEnded) {
                                if (pieces[currentRow][currentCol] != null && ((isWhiteTurn && pieces[currentRow][currentCol].isWhite()) || (!isWhiteTurn && !pieces[currentRow][currentCol].isWhite()))) {
                                    handlePieceClick(currentRow, currentCol, cellSize);
                                } else {
                                    handleNonEmptyCellClick(currentRow, currentCol, cellSize);
                                }
                            }
                        });
                    } else {
                        cell.setOnMouseClicked(event -> {
                            if (!gameEnded) {
                                handleEmptyCellClick(currentRow, currentCol, cellSize);
                            }
                        });
                    }
                    gridPane.add(cell, colonne, 7 - ligne);
                }
            }
            // Force la mise à jour du label de fin de partie après chaque rafraîchissement du plateau
            if (gameEnded && endGameLabel != null && !endGameLabel.getText().isEmpty()) {
                endGameLabel.setText(endGameLabel.getText()); // force le rafraîchissement
            }
        }

        private void handlePieceClick(int row, int col, double cellSize) {
            if (gameEnded) return;
            Piece piece = pieces[row][col];
            if (piece != null && ((isWhiteTurn && piece.isWhite()) || (!isWhiteTurn && !piece.isWhite()))) {
                selectedPieceRow = row;
                selectedPieceCol = col;
                highlightPossibleMoves(piece, cellSize);
            }
        }

        private void handleNonEmptyCellClick(int row, int col, double cellSize) {
            if (gameEnded) return;
            boolean moved = false;
            if (selectedPieceRow != -1) {
                Piece movingPiece = pieces[selectedPieceRow][selectedPieceCol];
                Piece targetPiece = pieces[row][col];
                if (isValidMove(selectedPieceRow, selectedPieceCol, row, col)) {
                    pieces[selectedPieceRow][selectedPieceCol] = null;
                    pieces[row][col] = movingPiece;
                    movingPiece.setRow(row);
                    movingPiece.setCol(col);
                    resetBoardColors(cellSize);
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;
                    isWhiteTurn = !isWhiteTurn;
                    turnLabel.setText(isWhiteTurn ? "Tour : Blancs" : "Tour : Noirs");
                    afficherPlateau(cellSize);
                    moved = true;
                }
            }
            if (moved || !gameEnded) checkEndGame();
        }

        private void handleEmptyCellClick(int row, int col, double cellSize) {
            if (gameEnded) return;
            boolean moved = false;
            if (selectedPieceRow != -1) {
                Piece movingPiece = pieces[selectedPieceRow][selectedPieceCol];
                if (isValidMove(selectedPieceRow, selectedPieceCol, row, col)) {
                    pieces[row][col] = movingPiece;
                    pieces[selectedPieceRow][selectedPieceCol] = null;
                    movingPiece.setRow(row);
                    movingPiece.setCol(col);
                    resetBoardColors(cellSize);
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;
                    isWhiteTurn = !isWhiteTurn;
                    turnLabel.setText(isWhiteTurn ? "Tour : Blancs" : "Tour : Noirs");
                    afficherPlateau(cellSize);
                    moved = true;
                }
            }
            if (moved || !gameEnded) checkEndGame();
        }

        private void checkEndGame() {
            if (!isKingPresent(true)) {
                showEndGame("Victoire des noirs !");
            } else if (!isKingPresent(false)) {
                showEndGame("Victoire des blancs !");
            } else {
                // S'assurer que le label est vide si la partie continue
                endGameLabel.setText("");
            }
        }

        private void showEndGame(String message) {
            endGameLabel.setText(message);
            endGameLabel.setVisible(true);
            endGameLabel.toFront();
            gameEnded = true;
            gridPane.setDisable(true);
        }

        // Rendre cette méthode publique pour la démo
        public void highlightPossibleMoves(Piece piece, double cellSize) {
            resetBoardColors(cellSize);
            // Surligne la case de la pièce sélectionnée
            if (piece != null) {
                int row = piece.getRow();
                int col = piece.getCol();
                StackPane selectedCell = (StackPane) getNodeFromGridPane(gridPane, col, 7 - row);
                if (selectedCell != null) {
                    selectedCell.setStyle("-fx-background-color: #4fc3f7; -fx-border-color: black; -fx-border-width: 1px; -fx-min-width: " + cellSize + "px; -fx-min-height: " + cellSize + "px;");
                }
                List<int[]> possibleMoves = piece.calculatePossibleMoves(getBoardState());
                for (int[] move : possibleMoves) {
                    int newRow = move[0];
                    int newCol = move[1];
                    StackPane cell = (StackPane) getNodeFromGridPane(gridPane, newCol, 7 - newRow);
                    if (cell != null) {
                        cell.setStyle("-fx-background-color: lightgreen; -fx-border-color: black; -fx-border-width: 1px; -fx-min-width: " + cellSize + "px; -fx-min-height: " + cellSize + "px;");
                    }
                }
            }
        }

        private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
            Piece piece = pieces[fromRow][fromCol];
            if (piece == null) {
                return false;
            }
            List<int[]> possibleMoves = piece.calculatePossibleMoves(getBoardState());
            for (int[] move : possibleMoves) {
                if (move[0] == toRow && move[1] == toCol) {
                    if (pieces[toRow][toCol] == null || pieces[toRow][toCol].isWhite() != piece.isWhite()) {
                        return true;
                    }
                }
            }
            return false;
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

        private void resetBoardColors(double cellSize) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    StackPane cell = (StackPane) getNodeFromGridPane(gridPane, j, 7 - i);
                    if (cell != null) {
                        cell.setStyle(getCellStyle(i, j, cellSize));
                    }
                }
            }
        }

        private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
            for (Node node : gridPane.getChildren()) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                    return node;
                }
            }
            return null;
        }

        private String getImagePath(Piece piece) {
            String color = piece.isWhite() ? "white" : "black";
            if (piece instanceof Rook) {
                return color + "-rook.png";
            } else if (piece instanceof Knight) {
                return color + "-knight.png";
            } else if (piece instanceof Bishop) {
                return color + "-bishop.png";
            } else if (piece instanceof Queen) {
                return color + "-queen.png";
            } else if (piece instanceof King) {
                return color + "-king.png";
            } else if (piece instanceof Pawn) {
                return color + "-pawn.png";
            }
            return "";
        }

        private String getCellStyle(int ligne, int colonne, double cellSize) {
            boolean isWhite = (ligne + colonne) % 2 == 0;
            return String.format("-fx-background-color: %s; -fx-border-color: black; -fx-border-width: 1px; -fx-min-width: %dpx; -fx-min-height: %dpx;",
                    isWhite ? "#f0d9b5" : "#b58863", (int) cellSize, (int) cellSize);
        }

        private boolean isKingPresent(boolean white) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece p = pieces[i][j];
                    if (p instanceof King && p.isWhite() == white) {
                        return true;
                    }
                }
            }
            return false;
        }

        // Permet à la démo de déplacer une pièce automatiquement
        public void simulateMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (gameEnded) return;
            Piece movingPiece = pieces[fromRow][fromCol];
            if (movingPiece != null && isValidMove(fromRow, fromCol, toRow, toCol)) {
                pieces[toRow][toCol] = movingPiece;
                pieces[fromRow][fromCol] = null;
                movingPiece.setRow(toRow);
                movingPiece.setCol(toCol);
                isWhiteTurn = !isWhiteTurn;
                turnLabel.setText(isWhiteTurn ? "Tour : Blancs" : "Tour : Noirs");
                afficherPlateau(60);
                checkEndGame();
            }
        }

        // Getter pour la démo automatique
        public boolean isWhiteTurn() {
            return isWhiteTurn;
        }

        // Pour la démo : surligne les coups possibles d'une pièce donnée
        public void highlightPossibleMovesDemo(int row, int col) {
            Piece piece = pieces[row][col];
            if (piece != null) {
                highlightPossibleMoves(piece, 60);
            }
        }

        // Getter pour accéder à une pièce à une position donnée (pour la démo)
        public Piece getPieceAt(int row, int col) {
            return pieces[row][col];
        }
    }
}
