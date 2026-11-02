package com.javamonk.intellireview.model;

import java.util.List;

public class CodeAnalysisResult {
    private boolean parsedSuccessfully;
    private String summary;
    private String parseMessage;
    private String timeComplexity;
    private String spaceComplexity;
    private List<String> codeSmells;
    private List<String> correctnessWarnings;
    private List<String> dsaPatterns;
    private List<String> optimizationSuggestions;
    private List<String> suggestions;
    private CodeStructure structure;
    private AiReviewResult aiReview;

    public boolean isParsedSuccessfully() {
        return parsedSuccessfully;
    }

    public void setParsedSuccessfully(boolean parsedSuccessfully) {
        this.parsedSuccessfully = parsedSuccessfully;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getParseMessage() {
        return parseMessage;
    }

    public void setParseMessage(String parseMessage) {
        this.parseMessage = parseMessage;
    }

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

    public List<String> getCorrectnessWarnings() {
        return correctnessWarnings;
    }

    public void setCorrectnessWarnings(List<String> correctnessWarnings) {
        this.correctnessWarnings = correctnessWarnings;
    }

    public List<String> getDsaPatterns() {
        return dsaPatterns;
    }

    public void setDsaPatterns(List<String> dsaPatterns) {
        this.dsaPatterns = dsaPatterns;
    }

    public List<String> getOptimizationSuggestions() {
        return optimizationSuggestions;
    }

    public void setOptimizationSuggestions(List<String> optimizationSuggestions) {
        this.optimizationSuggestions = optimizationSuggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public CodeStructure getStructure() {
        return structure;
    }

    public void setStructure(CodeStructure structure) {
        this.structure = structure;
    }

    public AiReviewResult getAiReview() {
        return aiReview;
    }

    public void setAiReview(AiReviewResult aiReview) {
        this.aiReview = aiReview;
    }
}
