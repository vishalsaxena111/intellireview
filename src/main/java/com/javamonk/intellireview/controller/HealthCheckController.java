package com.javamonk.intellireview.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/health")
public class HealthCheckController {


    @GetMapping( "/check")
    public ResponseEntity<String> check (){
        return ResponseEntity.ok( "current project is running now" + LocalDateTime.now());
    }

}
