package com.javamonk.intellireview.model;


public class CodeStructure {
    private int methodCount;
    private int forLoopCount;
    private int whileLoopCount;
    private boolean recursionDetected;

    // Getters and Setters
    public int getMethodCount() {
        return methodCount;
    }

    public void setMethodCount(int methodCount) {
        this.methodCount = methodCount;
    }

    public int getForLoopCount() {
        return forLoopCount;
    }

    public void setForLoopCount(int forLoopCount) {
        this.forLoopCount = forLoopCount;
    }

    public int getWhileLoopCount() {
        return whileLoopCount;
    }

    public void setWhileLoopCount(int whileLoopCount) {
        this.whileLoopCount = whileLoopCount;
    }

    public boolean isRecursionDetected() {
        return recursionDetected;
    }

    public void setRecursionDetected(boolean recursionDetected) {
        this.recursionDetected = recursionDetected;
    }
}
