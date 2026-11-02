package com.javamonk.intellireview.model;

import java.util.ArrayList;
import java.util.List;

public class AiReviewResult {
    private boolean enabled;
    private String model;
    private String status;
    private String summary;
    private List<String> correctnessIssues = new ArrayList<>();
    private List<String> complexityNotes = new ArrayList<>();
    private List<String> dsaPatterns = new ArrayList<>();
    private List<String> optimizationPlan = new ArrayList<>();
    private String improvedCode;
    private String confidence;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getCorrectnessIssues() {
        return correctnessIssues;
    }

    public void setCorrectnessIssues(List<String> correctnessIssues) {
        this.correctnessIssues = correctnessIssues;
    }

    public List<String> getComplexityNotes() {
        return complexityNotes;
    }

    public void setComplexityNotes(List<String> complexityNotes) {
        this.complexityNotes = complexityNotes;
    }

    public List<String> getDsaPatterns() {
        return dsaPatterns;
    }

    public void setDsaPatterns(List<String> dsaPatterns) {
        this.dsaPatterns = dsaPatterns;
    }

    public List<String> getOptimizationPlan() {
        return optimizationPlan;
    }

    public void setOptimizationPlan(List<String> optimizationPlan) {
        this.optimizationPlan = optimizationPlan;
    }

    public String getImprovedCode() {
        return improvedCode;
    }

    public void setImprovedCode(String improvedCode) {
        this.improvedCode = improvedCode;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
}
