package com.towerofhanoi.model;

import com.towerofhanoi.exception.InvalidMoveException;
import java.util.Stack;


// Represents one of the three rods (A, B, C).
public class Tower {
    private final String id;
    // We store Integers (sizes) rather than Disk objects.
    private final Stack<Integer> diskStack;

    public Tower(String id) {
        this.id = id;
        this.diskStack = new Stack<>();
    }

    public void addDisk(int diskSize) throws InvalidMoveException {
        if (!diskStack.isEmpty()) {
            int topSize = diskStack.peek();
            if (diskSize > topSize) {
                throw new InvalidMoveException("Cannot place larger disk (" + diskSize + 
                                               ") on smaller disk (" + topSize + ")");
            }
        }
        diskStack.push(diskSize);
    }

    public Integer removeDisk() {
        if (diskStack.isEmpty()) {
            return null;
        }
        return diskStack.pop();
    }

    public Integer peekDisk() {
        if (diskStack.isEmpty()) {
            return null;
        }
        return diskStack.peek();
    }

    public boolean isEmpty() {
        return diskStack.isEmpty();
    }

    public int getHeight() {
        return diskStack.size();
    }

    public String getId() {
        return id;
    }
    
    // Helper to get stack for View rendering (read-only copy recommended in production, but direct for simplicity here)
    public Stack<Integer> getDiskStack() {
        return diskStack;
    }
}