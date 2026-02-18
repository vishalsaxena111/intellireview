package com.javamonk.intellireview.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.stereotype.Service;

@Service
public class CodeParserService {

    public CodeStructure parseStructure(String codeSnippet) {
        CodeStructure structure = new CodeStructure();

        try {
            JavaParser parser = new JavaParser();
            ParseResult<CompilationUnit> result = parser.parse(codeSnippet);

            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();

                int methodCount = cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).size();
                structure.setMethodCount(methodCount);

                int forLoopCount = cu.findAll(com.github.javaparser.ast.stmt.ForStmt.class).size();
                structure.setForLoopCount(forLoopCount);

                int whileLoopCount = cu.findAll(com.github.javaparser.ast.stmt.WhileStmt.class).size();
                structure.setWhileLoopCount(whileLoopCount);

                boolean recursionDetected = cu.findAll(com.github.javaparser.ast.expr.MethodCallExpr.class).stream()
                        .anyMatch(call -> cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).stream()
                                .anyMatch(method -> method.getNameAsString().equals(call.getNameAsString())));
                structure.setRecursionDetected(recursionDetected);

            } else {
                System.out.println("Parsing failed: " + result.getProblems());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return structure;
    }
}
