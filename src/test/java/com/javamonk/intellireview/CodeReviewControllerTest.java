package com.javamonk.intellireview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CodeReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void analyzeCode_ShouldReturnValidAnalysis() throws Exception {
        String jsonRequest = "{ \"codeText\": \"public class Sample { public void foo() { for(int i=0;i<10;i++){} } }\" }";

        mockMvc.perform(post("/api/v1/code-review/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeComplexity").exists())
                .andExpect(jsonPath("$.spaceComplexity").exists())
                .andExpect(jsonPath("$.codeSmells").isArray())
                .andExpect(jsonPath("$.suggestions").isArray());
    }
}
