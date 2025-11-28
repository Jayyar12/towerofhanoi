package com.towerofhanoi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        // Load the initial scene
        loadScene("/fxml/WelcomeView.fxml", "Tower of Hanoi - Welcome");
        
        // Show the stage first
        stage.show();
        stage.centerOnScreen();
    }

    public static void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            double preferredWidth = 1200;
            double preferredHeight = 700;
            
            Scene scene = new Scene(root, preferredWidth, preferredHeight);
            
            // Load CSS
            scene.getStylesheets().add(Main.class.getResource("/styles/styles.css").toExternalForm());
            
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            
            // ADD THESE LINES to enforce size
            primaryStage.setMinWidth(preferredWidth);
            primaryStage.setMinHeight(preferredHeight);
            primaryStage.setWidth(preferredWidth);
            primaryStage.setHeight(preferredHeight);
            
            // Ensure the stage is not maximized
            primaryStage.setMaximized(false);
            
            // Center on screen
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}