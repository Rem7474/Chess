package view;

import javafx.application.Application;
import javafx.application.Platform;
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
    private static model.PiecePersonnalisee[][] pieces;
    private static model.PiecePersonnalisee[][] piecesClassiques = new model.PiecePersonnalisee[8][8];

    @Override
    public void afficher(Echiquier echiquier) {
        pieces = new model.PiecePersonnalisee[8][8];
        initializePieces(echiquier);
        JavaFXApp.echiquier = echiquier;
        Application.launch(JavaFXApp.class);
    }

    private static void initializePieces(Echiquier echiquier) {
        // Réinitialise le tableau
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = null;
                piecesClassiques[i][j] = null;
            }
        }
        // Place uniquement les pièces classiques au lancement
        try {
            java.util.List<model.PiecePersonnalisee> persos = model.PieceLoader.chargerDepuisJson("src/pieces.json");
            for (model.PiecePersonnalisee p : persos) {
                if (p.getRow() >= 0 && p.getRow() < 8 && p.getCol() >= 0 && p.getCol() < 8) {
                    String type = p.getType().toLowerCase();
                    if (type.equals("rook") || type.equals("knight") || type.equals("bishop") || type.equals("queen") || type.equals("king") || type.equals("pawn")) {
                        int code = echiquier.getEmplacement(p.getRow(), p.getCol());
                        if (code == p.getUnicode()) {
                            pieces[p.getRow()][p.getCol()] = p;
                            piecesClassiques[p.getRow()][p.getCol()] = p;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement pièces classiques (JavaFX) : " + e.getMessage());
        }
    }

    public static class JavaFXApp extends Application {
        public static Echiquier echiquier;
        private Stage primaryStage;
        private Label turnLabel;
        private Label chronoLabel;
        private Label endGameLabel;
        private GridPane gridPane;
        private boolean gameEnded = false;
        private long chronoStart = 0;
        private javafx.animation.Timeline chronoTimeline;
        private Button goButton;
        private Button demoButton;
        private boolean gameStarted = false;
        private boolean chronoStarted = false;
        private Demo demoInstance = null;
        private boolean demoPaused = false;

        @Override
        public void start(Stage primaryStage) {
            this.primaryStage = primaryStage;
            gridPane = new GridPane();
            double cellSize = 60;
            gridPane.setAlignment(Pos.CENTER);
            gridPane.setPrefSize(8 * cellSize, 8 * cellSize);

            // Correction : toujours réinitialiser les pièces à partir de l'échiquier courant
            AffichageJavaFX.initializePieces(echiquier);
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

            chronoLabel = new Label("Chrono : 00:00");
            chronoLabel.setStyle(
                "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976d2; -fx-padding: 8 20 8 20;"
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
            reloadButton.setOnMouseEntered(ev -> reloadButton.setStyle(reloadButton.getStyle() + "-fx-cursor: hand;"));
            reloadButton.setOnMouseExited(ev -> reloadButton.setStyle(reloadButton.getStyle().replace("-fx-cursor: hand;", "")));
            reloadButton.setOnAction(ev -> {
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
                stopChrono();
                chronoLabel.setText("Chrono : 00:00");
                chronoStarted = false;
                if (demoButton != null) demoButton.setText("Démo");
                demoInstance = null;
                demoPaused = false;
            });

            demoButton = new Button("Démo");
            demoButton.setStyle(
                "-fx-background-color: #2196F3; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px;" +
                "-fx-padding: 8 24 8 24; " +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
            demoButton.setOnAction(ev -> {
                if (demoInstance == null || !demoInstance.isRunning()) {
                    demoInstance = new Demo(this, echiquier, pieces); // Toujours recréer une instance propre
                    demoInstance.setRunning(true);
                    demoInstance.startDemo();
                    demoPaused = false;
                    demoButton.setText("Pause Démo");
                    startChrono();
                } else if (!demoPaused) {
                    demoInstance.pauseDemo();
                    demoPaused = true;
                    demoButton.setText("Reprendre Démo");
                } else {
                    demoInstance.resumeDemo();
                    demoPaused = false;
                    demoButton.setText("Pause Démo");
                    startChrono();
                }
            });

            HBox topBar = new HBox(30, turnLabel, chronoLabel, reloadButton, demoButton);
            topBar.setStyle("-fx-alignment: center; -fx-padding: 18; -fx-spacing: 30;");

            VBox centerBox = new VBox(20, gridPane, endGameLabel);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setFillWidth(false);

            // Création du conteneur principal en superposition
            StackPane rootStack = new StackPane();

            // --- Colonne de sélection des pièces personnalisées ---
            VBox leftBox = new VBox(16);
            leftBox.setStyle("-fx-background-color: #e0e7ef; -fx-padding: 18 8 18 8; -fx-border-radius: 8; -fx-background-radius: 8;");
            leftBox.setAlignment(Pos.TOP_CENTER);
            leftBox.getChildren().add(new Label("Pièces personnalisées :"));
            java.util.Map<String, Boolean> etatPerso = new java.util.HashMap<>();
            try {
                java.util.List<model.PiecePersonnalisee> persos = model.PieceLoader.chargerDepuisJson("src/pieces.json");
                java.util.Set<String> typesPerso = new java.util.HashSet<>();
                for (model.PiecePersonnalisee p : persos) {
                    if (!p.getType().equalsIgnoreCase("rook") && !p.getType().equalsIgnoreCase("knight") && !p.getType().equalsIgnoreCase("bishop") && !p.getType().equalsIgnoreCase("queen") && !p.getType().equalsIgnoreCase("king") && !p.getType().equalsIgnoreCase("pawn")) {
                        typesPerso.add(p.getType());
                        etatPerso.put(p.getType(), false);
                    }
                }
                for (String type : typesPerso) {
                    Button switchBtn = new Button("Activer " + type);
                    switchBtn.setStyle("-fx-background-color: #f59e42; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 18 6 18;");
                    switchBtn.setOnAction(ev -> {
                        boolean actif = etatPerso.get(type);
                        if (!actif) {
                            remplacerTypeSurPlateau(type);
                            switchBtn.setText("Désactiver " + type);
                            switchBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 18 6 18;");
                        } else {
                            restaurerTypeClassique(type);
                            switchBtn.setText("Activer " + type);
                            switchBtn.setStyle("-fx-background-color: #f59e42; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 6 18 6 18;");
                        }
                        etatPerso.put(type, !actif);
                        afficherPlateau(cellSize);
                    });
                    leftBox.getChildren().add(switchBtn);
                }
            } catch (Exception e) {
                leftBox.getChildren().add(new Label("Erreur chargement pièces perso"));
            }
            // --- Fin colonne personnalisée ---

            BorderPane root = new BorderPane();
            root.setLeft(leftBox);
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
                    model.PiecePersonnalisee piece = pieces[ligne][colonne];
                    StackPane cell = new StackPane();
                    cell.setStyle(getCellStyle(ligne, colonne, cellSize));
                    final int currentRow = ligne;
                    final int currentCol = colonne;
                    if (piece != null) {
                        String type = piece.getType().toLowerCase();
                        if (!type.equals("rook") && !type.equals("knight") && !type.equals("bishop") && !type.equals("queen") && !type.equals("king") && !type.equals("pawn")) {
                            Label emoji = new Label(new String(Character.toChars(piece.getUnicode())));
                            emoji.setStyle("-fx-font-size: 38px; -fx-font-weight: bold;");
                            cell.getChildren().add(emoji);
                        } else {
                            String imagePath = getImagePath(piece);
                            Image image = new Image(getClass().getResourceAsStream("/images/" + imagePath));
                            if (image != null) {
                                ImageView imageView = new ImageView(image);
                                imageView.setFitWidth(cellSize);
                                imageView.setFitHeight(cellSize);
                                cell.getChildren().add(imageView);
                            }
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
            if (!chronoStarted) startChrono();
            PiecePersonnalisee piece = pieces[row][col];
            if (piece != null && ((isWhiteTurn && piece.isWhite()) || (!isWhiteTurn && !pieces[row][col].isWhite()))) {
                selectedPieceRow = row;
                selectedPieceCol = col;
                highlightPossibleMoves(piece, cellSize);
            }
        }

        private void handleNonEmptyCellClick(int row, int col, double cellSize) {
            if (gameEnded) return;
            if (!chronoStarted) startChrono();
            boolean moved = false;
            if (selectedPieceRow != -1) {
                PiecePersonnalisee movingPiece = pieces[selectedPieceRow][selectedPieceCol];
                PiecePersonnalisee targetPiece = pieces[row][col];
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
            if (!chronoStarted) startChrono();
            boolean moved = false;
            if (selectedPieceRow != -1) {
                PiecePersonnalisee movingPiece = pieces[selectedPieceRow][selectedPieceCol];
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

        public void showEndGame(String message) {
            endGameLabel.setText(message);
            endGameLabel.setVisible(true);
            endGameLabel.toFront();
            gameEnded = true;
            gridPane.setDisable(true);
            stopChrono();
            if (demoButton != null) demoButton.setText("Démo");
            demoInstance = null;
            demoPaused = false;
        }

        public void showTemporaryMessage(String message, int timeoutMillis) {
            endGameLabel.setText(message);
            endGameLabel.setVisible(true);
            endGameLabel.toFront();
            new Thread(() -> {
                try { Thread.sleep(timeoutMillis); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    endGameLabel.setText("");
                    endGameLabel.setVisible(false);
                });
            }).start();
        }

        // Rendre cette méthode publique pour la démo
        public void highlightPossibleMoves(PiecePersonnalisee piece, double cellSize) {
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
            PiecePersonnalisee piece = pieces[fromRow][fromCol];
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

        private String getImagePath(model.PiecePersonnalisee piece) {
            return piece.getImagePath();
        }

        private String getCellStyle(int ligne, int colonne, double cellSize) {
            boolean isWhite = (ligne + colonne) % 2 == 0;
            return String.format("-fx-background-color: %s; -fx-border-color: black; -fx-border-width: 1px; -fx-min-width: %dpx; -fx-min-height: %dpx;",
                    isWhite ? "#f0d9b5" : "#b58863", (int) cellSize, (int) cellSize);
        }

        private boolean isKingPresent(boolean white) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    PiecePersonnalisee p = pieces[i][j];
                    if (p != null) {
                        if (p instanceof model.PiecePersonnalisee && p.isWhite() == white) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        // Chronomètre
        private void startChrono() {
            chronoStart = System.currentTimeMillis();
            chronoLabel.setText("Chrono : 00:00");
            if (chronoTimeline != null) chronoTimeline.stop();
            chronoTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> updateChrono())
            );
            chronoTimeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
            chronoTimeline.play();
            chronoStarted = true;
        }
        private void stopChrono() {
            if (chronoTimeline != null) chronoTimeline.stop();
            chronoStarted = false;
        }
        private void updateChrono() {
            long elapsed = (System.currentTimeMillis() - chronoStart) / 1000;
            long min = elapsed / 60;
            long sec = elapsed % 60;
            chronoLabel.setText(String.format("Chrono : %02d:%02d", min, sec));
        }

        // Permet à la démo de déplacer une pièce automatiquement
        public void simulateMove(int fromRow, int fromCol, int toRow, int toCol) {
            if (gameEnded) return;
            PiecePersonnalisee movingPiece = pieces[fromRow][fromCol];
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
            PiecePersonnalisee piece = pieces[row][col];
            if (piece != null) {
                highlightPossibleMoves(piece, 60);
            }
        }

        // Getter pour accéder à une pièce à une position donnée (pour la démo)
        public model.PiecePersonnalisee getPieceAt(int row, int col) {
            return pieces[row][col];
        }

        // Surligne une case de destination (avant déplacement en mode démo)
        public void highlightDestinationCell(int row, int col, double cellSize) {
            StackPane destCell = (StackPane) getNodeFromGridPane(gridPane, col, 7 - row);
            if (destCell != null) {
                destCell.setStyle("-fx-background-color: #ff9800; -fx-border-color: #d7263d; -fx-border-width: 2px; -fx-min-width: " + cellSize + "px; -fx-min-height: " + cellSize + "px;");
            }
        }

        // Surligne une case de destination sans bordure (pour la démo)
        public void highlightDestinationCellSansBorder(int row, int col, double cellSize) {
            StackPane destCell = (StackPane) getNodeFromGridPane(gridPane, col, 7 - row);
            if (destCell != null) {
                destCell.setStyle("-fx-background-color: #ff9800; -fx-border-color: black; -fx-border-width: 1px; -fx-min-width: " + cellSize + "px; -fx-min-height: " + cellSize + "px;");
            }
        }

        // Surligne une case de départ (avant déplacement en mode démo)
        public void highlightSelectedCell(int row, int col, double cellSize) {
            StackPane selectedCell = (StackPane) getNodeFromGridPane(gridPane, col, 7 - row);
            if (selectedCell != null) {
                selectedCell.setStyle("-fx-background-color: #4fc3f7; -fx-min-width: " + cellSize + "px; -fx-min-height: " + cellSize + "px;");
            }
        }

        // Remplace toutes les pièces classiques du type donné par les personnalisées, uniquement sur les deux premières (blancs) ou deux dernières (noirs) lignes
        private void remplacerTypeSurPlateau(String type) {
            try {
                java.util.List<model.PiecePersonnalisee> persos = model.PieceLoader.chargerDepuisJson("src/pieces.json");
                for (model.PiecePersonnalisee p : persos) {
                    if (p.getType().equalsIgnoreCase(type)) {
                        int row = p.getRow();
                        String t = p.getType().toLowerCase();
                        boolean isClassique = t.equals("rook") || t.equals("knight") || t.equals("bishop") || t.equals("queen") || t.equals("king") || t.equals("pawn");
                        if (!isClassique && !p.isWhite() && (row == 0 || row == 1)) {
                            // Pièce personnalisée noire : place-la sur la ligne inversée
                            int newRow = 7 - row;
                            pieces[newRow][p.getCol()] = new model.PiecePersonnalisee(p.getName(), p.getUnicode(), p.getImagePath(), newRow, p.getCol(), false, p.getType(), p.isKing(), p.getMovePattern());
                        } else if (!isClassique && p.isWhite() && (row == 0 || row == 1)) {
                            pieces[row][p.getCol()] = p;
                        } else if (isClassique && ((p.isWhite() && (row == 0 || row == 1)) || (!p.isWhite() && (row == 6 || row == 7)))) {
                            pieces[row][p.getCol()] = p;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur remplacement pièce personnalisée : " + e.getMessage());
            }
        }

        // Restaure les pièces classiques à leur position d'origine pour le type donné, uniquement sur les deux premières/dernières lignes
        private void restaurerTypeClassique(String type) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece classique = piecesClassiques[i][j];
                    if (classique != null && classique.getClass().getSimpleName().equalsIgnoreCase("PiecePersonnalisee") && ((model.PiecePersonnalisee)classique).getType().equalsIgnoreCase(type)) {
                        // Blancs : lignes 0 et 1, Noirs : lignes 6 et 7
                        if ((((model.PiecePersonnalisee)classique).isWhite() && (i == 0 || i == 1)) || (!((model.PiecePersonnalisee)classique).isWhite() && (i == 6 || i == 7))) {
                            pieces[i][j] = classique;
                        }
                    }
                }
            }
        }

        // Fonction utilitaire pour inverser la position d'une pièce personnalisée pour les noirs (ligne 0/1 -> 7/6)
        private model.PiecePersonnalisee reverseForBlack(model.PiecePersonnalisee p) {
            if (p.isWhite()) return p;
            int newRow = 7 - p.getRow();
            return new model.PiecePersonnalisee(p.getName(), p.getUnicode(), p.getImagePath(), newRow, p.getCol(), false, p.getType(), p.isKing(), p.getMovePattern());
        }

        public void switchPlayer() {
            isWhiteTurn = !isWhiteTurn;
            if (turnLabel != null) {
                turnLabel.setText(isWhiteTurn ? "Tour : Blancs" : "Tour : Noirs");
            }
        }
    }
}
