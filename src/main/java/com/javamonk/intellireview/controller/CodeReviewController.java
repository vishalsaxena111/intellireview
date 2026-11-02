package com.javamonk.intellireview.controller;

import com.javamonk.intellireview.model.CodeAnalysisResult;
import com.javamonk.intellireview.model.CodeSnippet;
import com.javamonk.intellireview.service.CodeReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/v1/code-review")
public class CodeReviewController {


    private final CodeReviewService codeReviewService;

    @Autowired
    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }



    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeCode(@RequestBody CodeSnippet codeSnippet) {
        if (codeSnippet == null || codeSnippet.getCodeText() == null || codeSnippet.getCodeText().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "codeText is required."));
        }

        if (codeSnippet.getLanguage() != null && !"java".equalsIgnoreCase(codeSnippet.getLanguage())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only Java code is supported right now."));
        }

        CodeAnalysisResult result = codeReviewService.analyzeCodeSnippet(codeSnippet.getCodeText());
        return ResponseEntity.ok(result);
    }

}
