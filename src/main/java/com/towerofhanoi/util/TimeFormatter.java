package com.towerofhanoi.util;

public class TimeFormatter {

    /**
     * Converts total seconds into a standard HH:MM:SS format.
     * Example: 65 seconds -> "00:01:05"
     */
    public static String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}