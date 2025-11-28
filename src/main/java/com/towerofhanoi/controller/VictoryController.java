package com.towerofhanoi.controller;

import com.towerofhanoi.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class VictoryController {

    @FXML private Label victoryMessage;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        // Set up button handlers
        if (playAgainButton != null) {
            playAgainButton.setOnAction(event -> playAgain());
        }

        if (exitButton != null) {
            exitButton.setOnAction(event -> exitToMenu());
        }
    }

    public void setGameStats(String timeFormatted, int totalMoves, int minMoves) {
        System.out.println("setGameStats called!");
        System.out.println("Time: " + timeFormatted);
        System.out.println("Total Moves: " + totalMoves);
        System.out.println("Min Moves: " + minMoves);
        System.out.println("victoryMessage is: " + (victoryMessage == null ? "NULL" : "NOT NULL"));
        
        if (victoryMessage != null) {
            String message = String.format("""
                You have successfully completed the Tower of Hanoi! 🎉
                Time: %s
                Total Moves: %d
                Minimum possible moves: %d
                %s""",
                timeFormatted, 
                totalMoves, 
                minMoves,
                getPerformanceMessage(totalMoves, minMoves)
            );
            victoryMessage.setText(message);
        } else {
            System.err.println("ERROR: victoryMessage Label is NULL!");
        }
    }

    /**
     * Get a performance message based on how close the player was to optimal
     */
    private String getPerformanceMessage(int totalMoves, int minMoves) {
        if (totalMoves == minMoves) {
            return "Perfect! You solved it optimally!";
        } else if (totalMoves <= minMoves + 5) {
            return "Excellent! Very close to optimal!";
        } else if (totalMoves <= minMoves * 1.5) {
            return "Great job! Well done!";
        } else {
            return "Good effort! Try again for a better score!";
        }
    }

    /**
     * Start a new game
     */
    private void playAgain() {
        Main.loadScene("/fxml/SetupView.fxml", "Tower of Hanoi - Setup");
    }

    /**
     * Return to the main menu
     */
    private void exitToMenu() {
        Main.loadScene("/fxml/WelcomeView.fxml", "Tower of Hanoi");
    }
}