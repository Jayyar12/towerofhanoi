package com.towerofhanoi.model;

/**
 * Represents a single disk in the Tower of Hanoi game.
 */
public class Disk implements Comparable<Disk> {
    private final int size;
    // We store a simple ID or hex string for color mapping in the View later
    private final String colorHex; 

    public Disk(int size, String colorHex) {
        this.size = size;
        this.colorHex = colorHex;
    }

    public int getSize() {
        return size;
    }

    public String getColorHex() {
        return colorHex;
    }

    @Override
    public int compareTo(Disk other) {
        // Returns negative if this disk is smaller, positive if larger
        return Integer.compare(this.size, other.size);
    }

    @Override
    public String toString() {
        return "Disk-" + size;
    }
}