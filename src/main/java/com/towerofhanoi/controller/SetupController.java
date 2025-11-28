package com.towerofhanoi.controller;

import com.towerofhanoi.Main;
import com.towerofhanoi.util.SoundManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.io.IOException;

public class SetupController {

    // --- Setup View Elements ---
    @FXML private Slider diskSlider;
    @FXML private Label diskCountLabel;
    @FXML private RadioButton radioA, radioB, radioC;
    @FXML private ToggleButton shuffleToggle, randomizeToggle;
    @FXML private Button playButton;
    @FXML private Button backButton;
    @FXML private Label victoryMessage;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        // 1. Setup Logic (Only run if we are on the Setup Screen)
        if (diskSlider != null) {
            // Sound when disk count changes
            diskSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (!oldVal.equals(newVal)) {
                    SoundManager.playDiskSelectSound(); // Add this sound method
                }
                diskCountLabel.setText(String.valueOf(newVal.intValue()));
            });
            
            configureToggle(shuffleToggle);
            configureToggle(randomizeToggle);
            playButton.setOnAction(event -> startGame());
            backButton.setOnAction(event -> goBack());
            
            // Add sound for radio buttons (initial rod selection)
            configureRadioButton(radioA);
            configureRadioButton(radioB);
            configureRadioButton(radioC);
        }

        // 2. Victory Logic (Only run if we are on the Victory Screen)
        if (playAgainButton != null) {
            playAgainButton.setOnAction(event -> playAgain());
        }

        if (exitButton != null) {
            exitButton.setOnAction(event -> goBack());
        }
    }

    /**
     * Add sound effect when selecting initial rod
     */
    private void configureRadioButton(RadioButton radio) {
        radio.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            if (isSelected) {
                SoundManager.playRodSelectSound(); // Add this sound method
            }
        });
    }

    /**
     * Helper to make the ToggleButton change Text and Color when clicked.
     */
    private void configureToggle(ToggleButton toggle) {
        updateToggleStyle(toggle, toggle.isSelected());
        toggle.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            updateToggleStyle(toggle, isSelected);
        });
    }

    private void updateToggleStyle(ToggleButton toggle, boolean isSelected) {
        if (isSelected) {
            SoundManager.playButtonClickSound();
            toggle.setText("ON");
            toggle.setStyle("-fx-background-color: #5fcfc8; -fx-text-fill: white; " +
                            "-fx-background-radius: 20; -fx-cursor: hand;");
        } else {
            SoundManager.playButtonClickSound();
            toggle.setText("OFF");
            toggle.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; " +
                            "-fx-background-radius: 20; -fx-cursor: hand;");
        }
    }

    private void playAgain() {
        // Loads the Welcome View (or Setup View depending on your preference)
        SoundManager.playButtonClickSound();
        Main.loadScene("/fxml/SetupView.fxml", "Tower of Hanoi");
    }

    private void goBack() {
        // Stop music when going back to menu
        SoundManager.playButtonClickSound();
        
        // Loads the Welcome View (or Setup View depending on your preference)
        Main.loadScene("/fxml/WelcomeView.fxml", "Tower of Hanoi");
    }

    private void startGame() {
        try {
            // Stop menu music before starting the game
            SoundManager.playButtonClickSound();
            SoundManager.stopBackgroundMusic();
            
            int numberOfDisks = (int) diskSlider.getValue();

            String startRod = "A";
            if (radioA.isSelected()) startRod = "A";
            else if (radioB.isSelected()) startRod = "B";
            else if (radioC.isSelected()) startRod = "C";

            boolean isShuffle = shuffleToggle.isSelected();
            boolean isRandomize = randomizeToggle.isSelected();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameView.fxml"));
            Parent root = loader.load();
            GameController gameController = loader.getController();

            gameController.initGame(numberOfDisks, startRod, isShuffle, isRandomize);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

            Main.getPrimaryStage().setScene(scene);
            Main.getPrimaryStage().setTitle("Tower of Hanoi - Playing");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}