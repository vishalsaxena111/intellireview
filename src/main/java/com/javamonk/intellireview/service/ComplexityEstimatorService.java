package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

@Service
public class ComplexityEstimatorService {

    public String estimateTimeComplexity(CodeStructure structure) {
        if (structure.isRecursionDetected()) {
            return "O(2^n)";
        }

        if (structure.getForLoopCount() >= 2 || structure.getWhileLoopCount() >= 2) {
            return "O(n^2)";
        }

        if (structure.getForLoopCount() == 1 || structure.getWhileLoopCount() == 1) {
            return "O(n)";
        }

        return "O(1)";
    }

    public String estimateSpaceComplexity(CodeStructure structure) {
        if (structure.isRecursionDetected()) {
            return "O(n) (due to call stack)";
        }

        // For simplicity, assuming space proportional to method + loops
        if (structure.getMethodCount() > 1) {
            return "O(n)";
        }

        return "O(1)";
    }

}


