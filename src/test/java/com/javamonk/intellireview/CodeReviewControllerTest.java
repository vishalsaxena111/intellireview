package com.javamonk.intellireview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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
                .andExpect(jsonPath("$.correctnessWarnings").isArray())
                .andExpect(jsonPath("$.dsaPatterns").isArray())
                .andExpect(jsonPath("$.optimizationSuggestions").isArray())
                .andExpect(jsonPath("$.structure.totalLoopCount").value(1))
                .andExpect(jsonPath("$.aiReview.enabled").value(false))
                .andExpect(jsonPath("$.suggestions").isArray());
    }

    @Test
    public void analyzeCode_ShouldRejectEmptyPayload() throws Exception {
        mockMvc.perform(post("/api/v1/code-review/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"codeText\": \"\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("codeText is required."));
    }

    @Test
    public void analyzeCode_ShouldWarnWhenLoopSkipsIndexedElements() throws Exception {
        String jsonRequest = "{ \"codeText\": \"public class Sample { public int sum(int[] nums) { int total = 0; for(int i=0;i<nums.length;i+=2){ total += nums[i]; } return total; } }\" }";

        mockMvc.perform(post("/api/v1/code-review/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correctnessWarnings[0]", containsString("skips values")))
                .andExpect(jsonPath("$.correctnessWarnings[0]", containsString("miss elements")));
    }

    @Test
    public void analyzeCode_ShouldReturnConciseSyntaxDiagnostic() throws Exception {
        String jsonRequest = "{ \"codeText\": \"public class Sample { public void foo() { int value = ; } }\" }";

        mockMvc.perform(post("/api/v1/code-review/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parsedSuccessfully").value(false))
                .andExpect(jsonPath("$.parseMessage", containsString("Syntax error at line")))
                .andExpect(jsonPath("$.parseMessage", org.hamcrest.Matchers.not(containsString("Problem stacktrace"))));
    }
}
