package com.javamonk.intellireview.model;

import java.util.ArrayList;
import java.util.List;

public class CodeStructure {
    private boolean parsedSuccessfully;
    private String parseMessage;
    private int classCount;
    private int methodCount;
    private int fieldCount;
    private int forLoopCount;
    private int whileLoopCount;
    private int doWhileLoopCount;
    private int foreachLoopCount;
    private int ifCount;
    private int switchCount;
    private int returnCount;
    private int maxLoopNestingDepth;
    private int maxConditionalDepth;
    private boolean recursionDetected;
    private int maxRecursiveCallsInMethod;
    private List<String> methodNames = new ArrayList<>();
    private List<String> recursiveMethods = new ArrayList<>();
    private List<String> methodCalls = new ArrayList<>();
    private List<String> variableNames = new ArrayList<>();
    private List<String> importedTypes = new ArrayList<>();
    private List<String> correctnessWarnings = new ArrayList<>();

    public boolean isParsedSuccessfully() {
        return parsedSuccessfully;
    }

    public void setParsedSuccessfully(boolean parsedSuccessfully) {
        this.parsedSuccessfully = parsedSuccessfully;
    }

    public String getParseMessage() {
        return parseMessage;
    }

    public void setParseMessage(String parseMessage) {
        this.parseMessage = parseMessage;
    }

    public int getClassCount() {
        return classCount;
    }

    public void setClassCount(int classCount) {
        this.classCount = classCount;
    }

    public int getMethodCount() {
        return methodCount;
    }

    public void setMethodCount(int methodCount) {
        this.methodCount = methodCount;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
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

    public int getDoWhileLoopCount() {
        return doWhileLoopCount;
    }

    public void setDoWhileLoopCount(int doWhileLoopCount) {
        this.doWhileLoopCount = doWhileLoopCount;
    }

    public int getForeachLoopCount() {
        return foreachLoopCount;
    }

    public void setForeachLoopCount(int foreachLoopCount) {
        this.foreachLoopCount = foreachLoopCount;
    }

    public int getIfCount() {
        return ifCount;
    }

    public void setIfCount(int ifCount) {
        this.ifCount = ifCount;
    }

    public int getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(int switchCount) {
        this.switchCount = switchCount;
    }

    public int getReturnCount() {
        return returnCount;
    }

    public void setReturnCount(int returnCount) {
        this.returnCount = returnCount;
    }

    public int getMaxLoopNestingDepth() {
        return maxLoopNestingDepth;
    }

    public void setMaxLoopNestingDepth(int maxLoopNestingDepth) {
        this.maxLoopNestingDepth = maxLoopNestingDepth;
    }

    public int getMaxConditionalDepth() {
        return maxConditionalDepth;
    }

    public void setMaxConditionalDepth(int maxConditionalDepth) {
        this.maxConditionalDepth = maxConditionalDepth;
    }

    public boolean isRecursionDetected() {
        return recursionDetected;
    }

    public void setRecursionDetected(boolean recursionDetected) {
        this.recursionDetected = recursionDetected;
    }

    public int getMaxRecursiveCallsInMethod() {
        return maxRecursiveCallsInMethod;
    }

    public void setMaxRecursiveCallsInMethod(int maxRecursiveCallsInMethod) {
        this.maxRecursiveCallsInMethod = maxRecursiveCallsInMethod;
    }

    public List<String> getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(List<String> methodNames) {
        this.methodNames = methodNames;
    }

    public List<String> getRecursiveMethods() {
        return recursiveMethods;
    }

    public void setRecursiveMethods(List<String> recursiveMethods) {
        this.recursiveMethods = recursiveMethods;
    }

    public List<String> getMethodCalls() {
        return methodCalls;
    }

    public void setMethodCalls(List<String> methodCalls) {
        this.methodCalls = methodCalls;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

    public void setVariableNames(List<String> variableNames) {
        this.variableNames = variableNames;
    }

    public List<String> getImportedTypes() {
        return importedTypes;
    }

    public void setImportedTypes(List<String> importedTypes) {
        this.importedTypes = importedTypes;
    }

    public List<String> getCorrectnessWarnings() {
        return correctnessWarnings;
    }

    public void setCorrectnessWarnings(List<String> correctnessWarnings) {
        this.correctnessWarnings = correctnessWarnings;
    }

    public int getTotalLoopCount() {
        return forLoopCount + whileLoopCount + doWhileLoopCount + foreachLoopCount;
    }
}
