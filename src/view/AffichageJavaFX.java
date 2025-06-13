package view;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.*;

import java.util.List;

public class AffichageJavaFX implements Affichage {

    private static int selectedPieceRow = -1;
    private static int selectedPieceCol = -1;
    private static boolean isWhiteTurn = true;
    private static Piece[][] pieces; // Tableau pour stocker les pièces

    @Override
    public void afficher(Echiquier echiquier) {
        pieces = new Piece[8][8];
        initializePieces(echiquier); // Initialiser les pièces
        JavaFXApp.launchJavaFX(echiquier);
    }

    private void initializePieces(Echiquier echiquier) {
        // Initialiser les pièces blanches
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

        // Initialiser les pièces noires
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
    }

    public static class JavaFXApp extends Application {
        private static Echiquier echiquier;
        private Stage primaryStage;

        public static void launchJavaFX(Echiquier echiquier) {
            JavaFXApp.echiquier = echiquier;
            launch();
        }

        @Override
        public void start(Stage primaryStage) {
            this.primaryStage = primaryStage;
            GridPane gridPane = new GridPane();
            double cellSize = 50;

            for (int ligne = 7; ligne >= 0; ligne--) {
                for (int colonne = 0; colonne < 8; colonne++) {
                    Piece piece = pieces[ligne][colonne];
                    StackPane cell = new StackPane();
                    cell.setStyle(getCellStyle(ligne, colonne, cellSize));

                    if (piece != null) {
                        String imagePath = getImagePath(piece);
                        Image image = new Image(getClass().getResourceAsStream("/images/" + imagePath));
                        if (image != null) {
                            ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(cellSize);
                            imageView.setFitHeight(cellSize);
                            cell.getChildren().add(imageView);
                        } else {
                            System.err.println("Image not found: " + imagePath);
                        }
                        final int currentRow = ligne;
                        final int currentCol = colonne;
                        cell.setOnMouseClicked(event -> {
                            if (pieces[currentRow][currentCol] != null && ((isWhiteTurn && pieces[currentRow][currentCol].isWhite()) || (!isWhiteTurn && !pieces[currentRow][currentCol].isWhite()))) {
                                handlePieceClick(currentRow, currentCol, gridPane, cellSize);
                            } else {
                                handleNonEmptyCellClick(currentRow, currentCol, gridPane, cellSize);
                            }
                        });
                    } else {
                        final int currentRow = ligne;
                        final int currentCol = colonne;
                        cell.setOnMouseClicked(event -> handleEmptyCellClick(currentRow, currentCol, gridPane, cellSize));
                    }
                    gridPane.add(cell, colonne, 7 - ligne);
                }
            }


            Scene scene = new Scene(gridPane, 400, 400);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        private void handlePieceClick(int row, int col, GridPane gridPane, double cellSize) {
            Piece piece = pieces[row][col];
            if (piece != null && ((isWhiteTurn && piece.isWhite()) || (!isWhiteTurn && !piece.isWhite()))) {
                selectedPieceRow = row;
                selectedPieceCol = col;
                highlightPossibleMoves(piece, gridPane, cellSize);
            }
        }
        private void handleNonEmptyCellClick(int row, int col, GridPane gridPane, double cellSize) {
            if (selectedPieceRow != -1) {
                Piece movingPiece = pieces[selectedPieceRow][selectedPieceCol];
                Piece targetPiece = pieces[row][col];
                if (isValidMove(selectedPieceRow, selectedPieceCol, row, col)) {
                    // Retirez la pièce de la position actuelle
                    pieces[selectedPieceRow][selectedPieceCol] = null;

                    // Déplacez la pièce vers la nouvelle position
                    pieces[row][col] = movingPiece;

                    // Mettez à jour la position de la pièce
                    movingPiece.setRow(row);
                    movingPiece.setCol(col);

                    // Réinitialisez les couleurs du plateau
                    resetBoardColors(gridPane, cellSize);

                    // Réinitialisez la sélection
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;

                    // Changez de tour
                    isWhiteTurn = !isWhiteTurn;

                    // Rafraîchissez le plateau
                    start(primaryStage);
                }
            }
        }



        private void handleEmptyCellClick(int row, int col, GridPane gridPane, double cellSize) {
        	System.out.println("coucou");
            if (selectedPieceRow != -1) {
                Piece movingPiece = pieces[selectedPieceRow][selectedPieceCol];
                if (isValidMove(selectedPieceRow, selectedPieceCol, row, col)) {
                    // Déplacez la pièce vers la nouvelle position
                    pieces[row][col] = movingPiece;
                    pieces[selectedPieceRow][selectedPieceCol] = null;

                    // Mettez à jour la position de la pièce
                    movingPiece.setRow(row);
                    movingPiece.setCol(col);

                    // Réinitialisez les couleurs du plateau
                    resetBoardColors(gridPane, cellSize);

                    // Réinitialisez la sélection
                    selectedPieceRow = -1;
                    selectedPieceCol = -1;

                    // Changez de tour
                    isWhiteTurn = !isWhiteTurn;

                    // Rafraîchissez le plateau
                    start(primaryStage);
                }
            }
        }



        private void highlightPossibleMoves(Piece piece, GridPane gridPane, double cellSize) {
            resetBoardColors(gridPane, cellSize);
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


        private boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
            Piece piece = pieces[fromRow][fromCol];
            if (piece == null) {
                return false;
            }

            List<int[]> possibleMoves = piece.calculatePossibleMoves(getBoardState());
            for (int[] move : possibleMoves) {
                if (move[0] == toRow && move[1] == toCol) {
                    // Vérifiez si la case cible est vide ou contient une pièce adverse
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

        private void resetBoardColors(GridPane gridPane, double cellSize) {
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
    }
}
