package com.towerofhanoi.model;

import com.towerofhanoi.exception.InvalidMoveException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class GameModel {
    private final Map<String, Tower> towers;
    
    private final Queue<String> moveLog;
    
    private final Stack<MoveRecord> undoStack;
    
    private int totalMoves;

    // Helper record for Undo functionality
    private record MoveRecord(String fromId, String toId) {}

    public GameModel() {
        towers = new HashMap<>();
        towers.put("A", new Tower("A"));
        towers.put("B", new Tower("B"));
        towers.put("C", new Tower("C"));
        
        // LinkedList implements Queue interface in Java
        moveLog = new LinkedList<>(); 
        undoStack = new Stack<>();
        totalMoves = 0;
    }

    /**
     * Resets the game with N disks on the specified start rod.
     */
    public void initializeGame(int numberOfDisks, String startRodId) {
        // Clear all towers
        towers.values().forEach(t -> t.getDiskStack().clear());
        moveLog.clear();
        undoStack.clear();
        totalMoves = 0;

        // Populate start tower (Largest disk at bottom, so loop N down to 1)
        try {
            Tower startTower = towers.get(startRodId);
            for (int i = numberOfDisks; i >= 1; i--) {
                startTower.addDisk(i); // No exception here because we are stacking logically
            }
        } catch (InvalidMoveException e) {
            // Should never happen during initialization logic
            e.printStackTrace();
        }
    }

    /**
     * Executes a move from one rod to another.
     */
    public void moveDisk(String fromId, String toId) throws InvalidMoveException {
        Tower fromTower = towers.get(fromId);
        Tower toTower = towers.get(toId);

        if (fromTower.isEmpty()) {
            throw new InvalidMoveException("Source tower " + fromId + " is empty.");
        }

        // 1. Peek at disk to move
        int diskSize = fromTower.peekDisk();

        // 2. Try to add to destination (Tower.java validation logic runs here)
        toTower.addDisk(diskSize);

        // 3. If successful (no exception thrown), actually remove from source
        fromTower.removeDisk();

        // 4. Update Game State
        totalMoves++;
        logMove(fromId, toId, diskSize);
        undoStack.push(new MoveRecord(fromId, toId));
    }
    
    private void logMove(String from, String to, int disk) {
        String logEntry = String.format("Move %d: Disk %d from %s to %s", totalMoves, disk, from, to);
        moveLog.add(logEntry);
        
        // Optional: Keep log size manageable
        if (moveLog.size() > 50) {
            moveLog.poll();
        }
        System.out.println(logEntry); // For debug console
    }

    public Tower getTower(String id) {
        return towers.get(id);
    }

    public int getTotalMoves() {
        return totalMoves;
    }
    
    public Queue<String> getMoveLog() {
        return moveLog;
    }
}