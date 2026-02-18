package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeAnalysisResult;
import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeReviewService {



      CodeParserService codeParserService;
      ComplexityEstimatorService complexityEstimatorService;
      CodeSmellClassifierService codeSmellClassifierService ;


    public CodeReviewService(CodeParserService codeParserService, ComplexityEstimatorService complexityEstimatorService, CodeSmellClassifierService codeSmellClassifierService) {
        this.codeParserService = codeParserService;
        this.complexityEstimatorService = complexityEstimatorService;
        this.codeSmellClassifierService = codeSmellClassifierService;
    }

    public CodeAnalysisResult analyzeCodeSnippet(String codeSnippet) {

        CodeAnalysisResult result = new CodeAnalysisResult();

        // Step 1: Parse code structure
        CodeStructure codeStructure = codeParserService.parseStructure(codeSnippet);

        // Step 2: Estimate Complexity
        String timeComplexity = complexityEstimatorService.estimateTimeComplexity(codeStructure);
        String spaceComplexity = complexityEstimatorService.estimateSpaceComplexity(codeStructure);

        // Step 3: Detect Code Smells using ML
        List<String> detectedSmells = codeSmellClassifierService.detectCodeSmells(codeStructure);

        // Step 4: Generate Suggestions based on detected smells
        List<String> suggestions = new ArrayList<>();
        if (detectedSmells.contains("Long Method")) {
            suggestions.add("Consider splitting the method into smaller methods.");
        }
        if (detectedSmells.contains("Recursion Detected")) {
            suggestions.add("Check for potential stack overflow in recursion.");
        }
        if (detectedSmells.contains("High Nesting")) {
            suggestions.add("Refactor to reduce nesting for better readability.");

        }

        // Set results
        result.setTimeComplexity(timeComplexity);
        result.setSpaceComplexity(spaceComplexity);
        result.setCodeSmells(detectedSmells);
        result.setSuggestions(suggestions);

        return result;
    }

}
