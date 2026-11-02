package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptimizationSuggestionService {

    public List<String> suggestOptimizations(CodeStructure structure, List<String> dsaPatterns) {
        List<String> suggestions = new ArrayList<>();

        if (!structure.isParsedSuccessfully()) {
            suggestions.add("Fix syntax errors first so the analyzer can inspect control flow and method calls.");
            return suggestions;
        }

        if (structure.getMaxLoopNestingDepth() >= 2) {
            suggestions.add("Nested loops may be reducible with hashing, sorting plus two pointers, or precomputed prefix data.");
        }

        if (structure.isRecursionDetected() && !dsaPatterns.contains("Dynamic Programming / Memoization")) {
            suggestions.add("For recursive solutions with overlapping subproblems, add memoization or convert to bottom-up dynamic programming.");
        }

        if (dsaPatterns.contains("Two Pointers")) {
            suggestions.add("Two-pointer logic is usually optimal after sorting or when the input is already ordered; verify pointer movement cannot skip valid answers.");
        }

        if (dsaPatterns.contains("Sliding Window")) {
            suggestions.add("Use a sliding window when the answer can be updated by adding the right element and removing the left element.");
        }

        if (dsaPatterns.contains("Hashing / Frequency Counting")) {
            suggestions.add("Hash-based lookup can reduce repeated scans from O(n^2) to O(n), at the cost of O(n) extra space.");
        }

        if (structure.getMaxConditionalDepth() > 3) {
            suggestions.add("Flatten deeply nested conditions with guard clauses or extracted helper methods.");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("The structure looks compact; focus next on edge cases, input constraints, and naming clarity.");
        }

        return suggestions;
    }
}
