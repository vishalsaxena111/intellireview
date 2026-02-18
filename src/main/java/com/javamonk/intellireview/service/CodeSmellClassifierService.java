package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeSmellClassifierService {

    public List<String> detectCodeSmells(CodeStructure structure) {
        List<String> codeSmells = new ArrayList<>();

        // Rule 1: Long Method (heuristic: method count > 5)
        if (structure.getMethodCount() > 5) {
            codeSmells.add("Long Method");
        }

        // Rule 2: High Nesting (heuristic: forLoopCount + whileLoopCount > 3)
        if ((structure.getForLoopCount() + structure.getWhileLoopCount()) > 3) {
            codeSmells.add("High Nesting");
        }

        // Rule 3: Recursion detected
        if (structure.isRecursionDetected()) {
            codeSmells.add("Recursion Detected");
        }

        return codeSmells;
    }
}
