package com.towerofhanoi.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {

    private static MediaPlayer sfxPlayer;
    private static MediaPlayer bgMusicPlayer;
    private static double globalVolume = 0.5;

    // --- SOUND EFFECTS ---
    public static void playMoveSound() {
        playSfx("/sounds/mixkit-game-ball-tap-2073.wav");
    }

    public static void playInvalidMoveSound() {
        playSfx("/sounds/mixkit-game-show-wrong-answer-buzz-950.wav", 0.3); // 30% volume
    }

    public static void playButtonClickSound() {
        playSfx("/sounds/mouse-click-331781.mp3");
    }

    // New sound for disk slider (subtle selection sound)
    public static void playDiskSelectSound() {
        playSfx("/sounds/Pop.mp3");
    }

    // New sound for rod selection (radio buttons)
    public static void playRodSelectSound() {
        playSfx("/sounds/mouse-click-331781.mp3");
    }

    // Original method - uses global volume
    private static void playSfx(String resourcePath) {
        playSfx(resourcePath, globalVolume);
    }

    // Overloaded method - allows custom volume multiplier
    private static void playSfx(String resourcePath, double volumeMultiplier) {
        try {
            if (sfxPlayer != null) {
                sfxPlayer.stop();
                sfxPlayer.dispose();
            }

            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("Sound file not found: " + resourcePath);
                return;
            }

            Media media = new Media(url.toExternalForm());
            sfxPlayer = new MediaPlayer(media);
            sfxPlayer.setVolume(globalVolume * volumeMultiplier); // Apply volume multiplier
            sfxPlayer.play();

            sfxPlayer.setOnEndOfMedia(() -> {
                sfxPlayer.dispose();
                sfxPlayer = null;
            });

        } catch (Exception e) {
            System.err.println("Error playing sound: " + resourcePath);
            e.printStackTrace();
        }
    }

    // --- BACKGROUND MUSIC ---
    private static void playBackgroundMusic(String resourcePath) {
        try {
            stopBackgroundMusic();

            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("Background music file not found: " + resourcePath);
                return;
            }

            Media media = new Media(url.toExternalForm());
            bgMusicPlayer = new MediaPlayer(media);
            bgMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgMusicPlayer.setVolume(globalVolume);
            bgMusicPlayer.play();

        } catch (Exception e) {
            System.err.println("Error playing background music: " + resourcePath);
            e.printStackTrace();
        }
    }

    public static void stopBackgroundMusic() {
        if (bgMusicPlayer != null) {
            bgMusicPlayer.stop();
            bgMusicPlayer.dispose();
            bgMusicPlayer = null;
        }
    }

    // --- PREDEFINED MENU MUSIC ---
    public static void playMenuMusic() {
        playBackgroundMusic("/sounds/soft-suspense-music-28126.mp3");
    }

    // --- VOLUME CONTROL ---
    public static void setVolume(double volume) {
        globalVolume = Math.max(0.0, Math.min(0.1, volume));
        if (sfxPlayer != null) sfxPlayer.setVolume(globalVolume);
        if (bgMusicPlayer != null) bgMusicPlayer.setVolume(globalVolume);
    }
}