package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DsaPatternDetectorService {

    public List<String> detectPatterns(CodeStructure structure) {
        List<String> patterns = new ArrayList<>();
        String searchableText = searchableText(structure);

        if (!structure.isParsedSuccessfully()) {
            return patterns;
        }

        if (containsAny(searchableText, "left", "right", "start", "end")
                && structure.getTotalLoopCount() > 0) {
            patterns.add("Two Pointers");
        }

        if (containsAny(searchableText, "window", "left", "right")
                && containsAny(searchableText, "sum", "count", "frequency")) {
            patterns.add("Sliding Window");
        }

        if (containsAny(searchableText, "mid", "binarysearch")
                || containsAny(structure.getMethodCalls(), "binarySearch")) {
            patterns.add("Binary Search");
        }

        if (containsAny(searchableText, "dp", "memo", "cache")) {
            patterns.add("Dynamic Programming / Memoization");
        }

        if (structure.isRecursionDetected()) {
            patterns.add("Recursion");
        }

        if (containsAny(searchableText, "queue", "poll", "offer")) {
            patterns.add("Breadth First Search / Queue Processing");
        }

        if (containsAny(searchableText, "stack", "push", "pop")) {
            patterns.add("Stack Processing / Depth First Search");
        }

        if (containsAny(searchableText, "map", "hashmap", "set", "hashset", "frequency")) {
            patterns.add("Hashing / Frequency Counting");
        }

        if (containsAny(structure.getMethodCalls(), "sort", "sorted")) {
            patterns.add("Sorting");
        }

        if (patterns.isEmpty()) {
            patterns.add("Straight-line / Basic Iteration");
        }

        return patterns;
    }

    private String searchableText(CodeStructure structure) {
        return String.join(" ",
                String.join(" ", structure.getMethodNames()),
                String.join(" ", structure.getMethodCalls()),
                String.join(" ", structure.getVariableNames()),
                String.join(" ", structure.getImportedTypes()))
                .toLowerCase(Locale.ROOT);
    }

    private boolean containsAny(String value, String... tokens) {
        for (String token : tokens) {
            if (value.contains(token.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(List<String> values, String... tokens) {
        String joinedValues = String.join(" ", values).toLowerCase(Locale.ROOT);
        return containsAny(joinedValues, tokens);
    }
}
