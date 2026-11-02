package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeAnalysisResult;
import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeReviewService {



      private final CodeParserService codeParserService;
      private final ComplexityEstimatorService complexityEstimatorService;
      private final CodeSmellClassifierService codeSmellClassifierService;
      private final DsaPatternDetectorService dsaPatternDetectorService;
      private final OptimizationSuggestionService optimizationSuggestionService;
      private final AiCodeReviewService aiCodeReviewService;


    public CodeReviewService(CodeParserService codeParserService,
                             ComplexityEstimatorService complexityEstimatorService,
                             CodeSmellClassifierService codeSmellClassifierService,
                             DsaPatternDetectorService dsaPatternDetectorService,
                             OptimizationSuggestionService optimizationSuggestionService,
                             AiCodeReviewService aiCodeReviewService) {
        this.codeParserService = codeParserService;
        this.complexityEstimatorService = complexityEstimatorService;
        this.codeSmellClassifierService = codeSmellClassifierService;
        this.dsaPatternDetectorService = dsaPatternDetectorService;
        this.optimizationSuggestionService = optimizationSuggestionService;
        this.aiCodeReviewService = aiCodeReviewService;
    }

    public CodeAnalysisResult analyzeCodeSnippet(String codeSnippet) {

        CodeAnalysisResult result = new CodeAnalysisResult();

        // Step 1: Parse code structure
        CodeStructure codeStructure = codeParserService.parseStructure(codeSnippet);

        // Step 2: Estimate Complexity
        String timeComplexity = complexityEstimatorService.estimateTimeComplexity(codeStructure);
        String spaceComplexity = complexityEstimatorService.estimateSpaceComplexity(codeStructure);

        // Step 3: Detect code smells with deterministic static-analysis rules
        List<String> detectedSmells = codeSmellClassifierService.detectCodeSmells(codeStructure);

        // Step 4: Detect DSA patterns and optimization opportunities
        List<String> dsaPatterns = dsaPatternDetectorService.detectPatterns(codeStructure);
        List<String> optimizationSuggestions = optimizationSuggestionService.suggestOptimizations(codeStructure, dsaPatterns);

        // Step 5: Generate Suggestions based on detected smells
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
        if (suggestions.isEmpty()) {
            suggestions.add("No major code smells detected by the current rules.");
        }

        // Set results
        result.setParsedSuccessfully(codeStructure.isParsedSuccessfully());
        result.setParseMessage(codeStructure.getParseMessage());
        result.setSummary(summaryFor(codeStructure, timeComplexity, spaceComplexity));
        result.setTimeComplexity(timeComplexity);
        result.setSpaceComplexity(spaceComplexity);
        result.setCodeSmells(detectedSmells);
        result.setCorrectnessWarnings(codeStructure.getCorrectnessWarnings());
        result.setDsaPatterns(dsaPatterns);
        result.setOptimizationSuggestions(optimizationSuggestions);
        result.setSuggestions(suggestions);
        result.setStructure(codeStructure);
        result.setAiReview(aiCodeReviewService.review(codeSnippet, codeStructure, detectedSmells, dsaPatterns));

        return result;
    }

    private String summaryFor(CodeStructure codeStructure, String timeComplexity, String spaceComplexity) {
        if (!codeStructure.isParsedSuccessfully()) {
            return "The submitted code could not be parsed. Fix syntax issues and try again.";
        }

        return "Detected " + codeStructure.getMethodCount()
                + " method(s), " + codeStructure.getTotalLoopCount()
                + " loop(s), estimated time " + timeComplexity
                + " and space " + spaceComplexity + ".";
    }
}
