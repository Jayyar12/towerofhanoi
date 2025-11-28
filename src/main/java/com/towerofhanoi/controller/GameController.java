package com.towerofhanoi.controller;

import com.towerofhanoi.Main;
import com.towerofhanoi.exception.InvalidMoveException;
import com.towerofhanoi.model.GameModel;
import com.towerofhanoi.model.Tower;
import com.towerofhanoi.util.TimeFormatter;
import com.towerofhanoi.util.MoveValidator;
import com.towerofhanoi.util.SoundManager;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.Stack;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;

public class GameController {

    @FXML private Label moveCounter;
    @FXML private Label timeLabel; 
    @FXML private Button menuButton;
    @FXML private VBox rodA, rodB, rodC;

    private GameModel gameModel;
    private int totalDisks;
    private boolean isShuffleOn;
    private boolean isRandomizeOn;
    private String targetRodId = "C";
    private Timeline gameTimer;
    private long elapsedSeconds = 0;

    private final List<String> DISK_COLORS = Arrays.asList(
        "#FF6B6B", "#4ECDC4", "#45B7D1", "#FFA07A", 
        "#98FB98", "#DDA0DD", "#F0E68C", "#87CEEB"
    );

    private void resumeGame() {
        System.out.println("Resuming game. Timer is: " + (gameTimer != null ? "Active" : "NULL"));
        if (gameTimer != null) {
            SoundManager.playButtonClickSound();
            gameTimer.play();
            System.out.println("Timer resumed!");
        }
    }

    @FXML
    public void initialize() {
        gameModel = new GameModel();

        setupRodDragEvents(rodA, "A");
        setupRodDragEvents(rodB, "B");
        setupRodDragEvents(rodC, "C");

        menuButton.setOnAction(e -> showOptions());
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void initGame(int numberOfDisks, String startRod, boolean isShuffle, boolean isRandomize) {
        this.totalDisks = numberOfDisks;
        this.isShuffleOn = isShuffle;
        this.isRandomizeOn = isRandomize;

        if (isShuffle) {
            Collections.shuffle(DISK_COLORS);
        }

        gameModel.initializeGame(numberOfDisks, startRod);
        if (isRandomize) {
            gameModel.getTower("A").getDiskStack().clear();
            gameModel.getTower("B").getDiskStack().clear();
            gameModel.getTower("C").getDiskStack().clear();

            // Distribute disks from largest to smallest
            Random random = new Random();
            String[] rods = {"A", "B", "C"};
            try {
                for (int i = numberOfDisks; i >= 1; i--) {
                    String randomRodId = rods[random.nextInt(3)];
                    gameModel.getTower(randomRodId).addDisk(i);
                }
            } catch (InvalidMoveException e) {
                e.printStackTrace();
            }
        }
        // Determine target rod based on start rod
        switch (startRod) {
            case "A":
                this.targetRodId = "C";
                break;
            case "B":
                this.targetRodId = "C";
                break;
            case "C":
                this.targetRodId = "A";
                break;
            default:
                throw new IllegalArgumentException("Invalid start rod: " + startRod);
        }
        
        renderTowers();
        updateHUD();

        startTimer();
    }


    private void startTimer() {
        stopTimer();
        
        elapsedSeconds = 0;
        timeLabel.setText("00:00:00");

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            elapsedSeconds++;
            timeLabel.setText(TimeFormatter.formatTime(elapsedSeconds));
        }));
        
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void stopTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void renderTowers() {
        renderSingleTower("A", rodA);
        renderSingleTower("B", rodB);
        renderSingleTower("C", rodC);
    }
    
    private void renderSingleTower(String towerId, VBox visualRod) {
        visualRod.getChildren().clear();
        Tower tower = gameModel.getTower(towerId);
        Stack<Integer> disks = tower.getDiskStack();
        for (int i = disks.size() - 1; i >= 0; i--) {
            int size = disks.get(i);
            visualRod.getChildren().add(createDiskNode(size, towerId));
        }
    }

    private StackPane createDiskNode(int size, String currentTowerId) {
        StackPane diskGroup = new StackPane();
        double width = 40 + (size * 25);
        double height = 25;
        Rectangle rect = new Rectangle(width, height);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setFill(Color.web(DISK_COLORS.get((size - 1) % DISK_COLORS.size())));
        rect.setStroke(Color.DARKSLATEGRAY);
        Text text = new Text(String.valueOf(size));
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: bold;");
        diskGroup.getChildren().addAll(rect, text);

        Tower tower = gameModel.getTower(currentTowerId);
        boolean isTop = !tower.isEmpty() && tower.peekDisk() == size;

        if (isTop) {
            diskGroup.setOnDragDetected(event -> {
                Dragboard db = diskGroup.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(currentTowerId);
                db.setContent(content);
                db.setDragView(diskGroup.snapshot(null, null));
                event.consume();
            });
            diskGroup.setCursor(javafx.scene.Cursor.OPEN_HAND);
        }
        return diskGroup;
    }

    private void setupRodDragEvents(VBox targetRod, String targetId) {
        targetRod.setOnDragOver(event -> {
            if (event.getGestureSource() != targetRod && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        targetRod.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String sourceId = db.getString();
                try {
                    gameModel.moveDisk(sourceId, targetId);
                    success = true;
                    
                    // PLAY SOUND ON VALID MOVE
                    SoundManager.playMoveSound();
                    
                    renderTowers();
                    updateHUD();
                    checkWinCondition();
                } catch (InvalidMoveException e) {
                    System.out.println("Invalid Move: " + e.getMessage());
                    
                    // PLAY SOUND ON INVALID MOVE
                    SoundManager.playInvalidMoveSound();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void updateHUD() {
        moveCounter.setText(String.valueOf(gameModel.getTotalMoves()));
    }


    private void checkWinCondition() {
        Tower target = gameModel.getTower(targetRodId);
        
        if (target.getHeight() == totalDisks) {
            stopTimer();
            showWinDialog();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void showWinDialog() {
        int minMoves = MoveValidator.calculateMinMoves(totalDisks);
        try {
            var resource = getClass().getResource("/fxml/Victory.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent victoryOverlay = loader.load();
            VictoryController victoryController = loader.getController();

            victoryController.setGameStats(timeLabel.getText(), gameModel.getTotalMoves(), minMoves);

            // Get the current scene's root
            Stage stage = Main.getPrimaryStage();
            Scene currentScene = stage.getScene();
            Parent currentRoot = currentScene.getRoot();

            // Create a StackPane to hold both the game and victory overlay
            StackPane overlayContainer = new StackPane();
            overlayContainer.getChildren().addAll(currentRoot, victoryOverlay);

            // Set initial state for animations
            victoryOverlay.setOpacity(0);
            victoryOverlay.setScaleX(0.8);
            victoryOverlay.setScaleY(0.8);

            // Create new scene with the overlay
            Scene overlayScene = new Scene(overlayContainer);
            overlayScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            stage.setScene(overlayScene);
            stage.setTitle("Victory!");

            // Animate the victory screen entrance
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), victoryOverlay);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), victoryOverlay);
            scaleIn.setFromX(0.8);
            scaleIn.setFromY(0.8);
            scaleIn.setToX(1);
            scaleIn.setToY(1);
            scaleIn.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

            ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);
            entrance.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void showOptions() {
        if (gameTimer != null) gameTimer.pause();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OptionsView.fxml"));
            Parent optionsOverlay = loader.load();

            Scene currentScene = menuButton.getScene();
            Parent gameRoot = currentScene.getRoot();

            StackPane overlayContainer = new StackPane();
            
            if (gameRoot.getParent() != null) {
                ((StackPane) gameRoot.getParent()).getChildren().remove(gameRoot);
            }
            
            overlayContainer.getChildren().addAll(gameRoot, optionsOverlay);
            currentScene.setRoot(overlayContainer);

            Button resumeButton = (Button) optionsOverlay.lookup("#resumeButton");
            Button restartButton = (Button) optionsOverlay.lookup("#restartButton");
            Button leaveButton = (Button) optionsOverlay.lookup("#leaveButton");

            Runnable closeOverlay = () -> {
                overlayContainer.getChildren().remove(gameRoot);
                currentScene.setRoot(gameRoot);
            };

            // Resume Game
            resumeButton.setOnAction(e -> {
                SoundManager.playButtonClickSound();
                closeOverlay.run();
                resumeGame();
            });

            restartButton.setOnAction(e -> {
                SoundManager.playButtonClickSound();
                stopTimer();
                try {
                    FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/fxml/GameView.fxml"));
                    Parent newGameRoot = gameLoader.load();
                    GameController newController = gameLoader.getController();
                    
                    newController.initGame(totalDisks, "A", isShuffleOn, isRandomizeOn);
                    
                    Scene newScene = new Scene(newGameRoot, 1200, 700);
                    newScene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
                    
                    Stage stage = Main.getPrimaryStage();
                    stage.setScene(newScene);
                    stage.centerOnScreen();
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Leave Game
            leaveButton.setOnAction(e -> {
                stopTimer();
                Main.loadScene("/fxml/WelcomeView.fxml", "Tower of Hanoi");
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}