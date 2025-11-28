package com.towerofhanoi.util;

public class MoveValidator {

    /**
     * Calculates the minimum number of moves required to solve the Tower of Hanoi.
     * Formula: 2^n - 1
     */
    public static int calculateMinMoves(int numberOfDisks) {
        return (int) Math.pow(2, numberOfDisks) - 1;
    }
}