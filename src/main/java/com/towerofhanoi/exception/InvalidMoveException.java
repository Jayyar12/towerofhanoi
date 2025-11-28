package com.towerofhanoi.exception;

/**
 * Thrown when a player attempts a move that violates Tower of Hanoi rules.
 * Rule: A larger disk cannot be placed on top of a smaller disk.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}