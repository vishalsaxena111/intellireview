package com.javamonk.intellireview.service;

import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CodeSmellClassifierService {

    public List<String> detectCodeSmells(CodeStructure structure) {
        List<String> codeSmells = new ArrayList<>();

        if (!structure.isParsedSuccessfully()) {
            codeSmells.add("Code could not be parsed");
            return codeSmells;
        }

        if (structure.getMethodCount() > 8) {
            codeSmells.add("Long Method");
        }

        if (structure.getMaxLoopNestingDepth() > 2 || structure.getMaxConditionalDepth() > 3) {
            codeSmells.add("High Nesting");
        }

        if (structure.isRecursionDetected()) {
            codeSmells.add("Recursion Detected");
        }

        if (structure.getTotalLoopCount() > 4) {
            codeSmells.add("Loop Heavy Implementation");
        }

        if (structure.getReturnCount() > 6) {
            codeSmells.add("Many Return Paths");
        }

        return codeSmells;
    }
}
