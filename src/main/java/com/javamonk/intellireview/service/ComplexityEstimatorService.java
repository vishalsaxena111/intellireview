package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

@Service
public class ComplexityEstimatorService {

    public String estimateTimeComplexity(CodeStructure structure) {
        if (structure.isRecursionDetected()) {
            if (structure.getMaxRecursiveCallsInMethod() > 1) {
                return "O(2^n)";
            }
            return "O(n)";
        }

        if (structure.getMaxLoopNestingDepth() >= 3) {
            return "O(n^" + structure.getMaxLoopNestingDepth() + ")";
        }

        if (structure.getMaxLoopNestingDepth() == 2) {
            return "O(n^2)";
        }

        if (structure.getTotalLoopCount() > 0) {
            return "O(n)";
        }

        return "O(1)";
    }

    public String estimateSpaceComplexity(CodeStructure structure) {
        if (structure.isRecursionDetected()) {
            return "O(n) (due to call stack)";
        }

        boolean hasGrowingDataStructure = structure.getImportedTypes().stream()
                .anyMatch(type -> type.contains("List")
                        || type.contains("Map")
                        || type.contains("Set")
                        || type.contains("Queue")
                        || type.contains("Stack"));

        if (hasGrowingDataStructure) {
            return "O(n)";
        }

        return "O(1)";
    }

}


