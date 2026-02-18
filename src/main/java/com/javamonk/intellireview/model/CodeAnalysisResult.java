package com.javamonk.intellireview.model;

import java.util.List;

public class CodeAnalysisResult {
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> codeSmells;
    private List<String> suggestions;

    // Getters and Setters

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public List<String> getCodeSmells() {
        return codeSmells;
    }

    public void setCodeSmells(List<String> codeSmells) {
        this.codeSmells = codeSmells;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
