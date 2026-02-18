package com.javamonk.intellireview.controller;

import com.javamonk.intellireview.model.CodeAnalysisResult;
import com.javamonk.intellireview.model.CodeSnippet;
import com.javamonk.intellireview.service.CodeReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/code-review")
public class CodeReviewController {


    CodeReviewService codeReviewService;

    @Autowired
    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }



    @PostMapping("/analyze")
    public ResponseEntity<CodeAnalysisResult> analyzeCode(@RequestBody CodeSnippet codeSnippet) {
        CodeAnalysisResult result = codeReviewService.analyzeCodeSnippet(codeSnippet.getCodeText());
        return ResponseEntity.ok(result);
    }

}
