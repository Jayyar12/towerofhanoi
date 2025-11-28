package com.towerofhanoi.controller;

import com.towerofhanoi.Main;
import com.towerofhanoi.util.SoundManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class MenuController {

    @FXML private Button startButton;
    @FXML private Button exitGameButton;

    @FXML
    public void initialize() {
        
        // Start background music when menu loads
        SoundManager.playMenuMusic();
        
        // Event Listener for Start Button
        startButton.setOnAction(event -> {
            // Navigate to Setup View
            SoundManager.playButtonClickSound();
            Main.loadScene("/fxml/SetupView.fxml", "Tower of Hanoi - Setup");
        });
    
        exitGameButton.setOnAction(e -> {
            // Stop music before exiting
            SoundManager.stopBackgroundMusic();
            Platform.exit();
            System.exit(0);
        });
    }
}