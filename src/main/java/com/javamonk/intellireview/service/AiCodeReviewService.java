package com.javamonk.intellireview.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javamonk.intellireview.model.AiReviewResult;
import com.javamonk.intellireview.model.CodeStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiCodeReviewService {

    private final ObjectMapper objectMapper;
    private final RestClient openAiClient;
    private final RestClient groqClient;
    private final RestClient ollamaClient;
    private final String provider;
    private final String openAiApiKey;
    private final String openAiModel;
    private final String groqApiKey;
    private final String groqModel;
    private final String ollamaModel;

    public AiCodeReviewService(ObjectMapper objectMapper,
                               @Value("${ai.provider:openai}") String provider,
                               @Value("${openai.api-key:}") String openAiApiKey,
                               @Value("${openai.model:gpt-5.5}") String openAiModel,
                               @Value("${openai.base-url:https://api.openai.com/v1}") String openAiBaseUrl,
                               @Value("${groq.api-key:}") String groqApiKey,
                               @Value("${groq.model:openai/gpt-oss-120b}") String groqModel,
                               @Value("${groq.base-url:https://api.groq.com/openai/v1}") String groqBaseUrl,
                               @Value("${ollama.model:qwen2.5-coder:7b}") String ollamaModel,
                               @Value("${ollama.base-url:http://localhost:11434}") String ollamaBaseUrl) {
        this.objectMapper = objectMapper;
        this.provider = provider;
        this.openAiApiKey = openAiApiKey;
        this.openAiModel = openAiModel;
        this.groqApiKey = groqApiKey;
        this.groqModel = groqModel;
        this.ollamaModel = ollamaModel;
        this.openAiClient = RestClient.builder()
                .baseUrl(openAiBaseUrl)
                .build();
        this.groqClient = RestClient.builder()
                .baseUrl(groqBaseUrl)
                .build();
        this.ollamaClient = RestClient.builder()
                .baseUrl(ollamaBaseUrl)
                .build();
    }

    public AiReviewResult review(String codeSnippet,
                                 CodeStructure structure,
                                 List<String> deterministicSmells,
                                 List<String> deterministicPatterns) {
        if ("ollama".equalsIgnoreCase(provider)) {
            return reviewWithOllama(codeSnippet, structure, deterministicSmells, deterministicPatterns);
        }

        if ("groq".equalsIgnoreCase(provider)) {
            if (groqApiKey == null || groqApiKey.isBlank()) {
                return disabledResult(groqModel, "Set GROQ_API_KEY to enable Groq AI review.");
            }
            return reviewWithGroq(codeSnippet, structure, deterministicSmells, deterministicPatterns);
        }

        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            return disabledResult();
        }

        return reviewWithOpenAi(codeSnippet, structure, deterministicSmells, deterministicPatterns);
    }

    private AiReviewResult disabledResult() {
        return disabledResult(openAiModel, "Set OPENAI_API_KEY, GROQ_API_KEY, or use AI_PROVIDER=ollama to enable AI agent review.");
    }

    private AiReviewResult disabledResult(String model, String message) {
        AiReviewResult result = new AiReviewResult();
        result.setEnabled(false);
        result.setModel(model);
        result.setStatus(message);
        return result;
    }

    private AiReviewResult reviewWithGroq(String codeSnippet,
                                          CodeStructure structure,
                                          List<String> deterministicSmells,
                                          List<String> deterministicPatterns) {
        try {
            String response = groqClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + groqApiKey)
                    .body(groqRequestPayload(codeSnippet, structure, deterministicSmells, deterministicPatterns))
                    .retrieve()
                    .body(String.class);

            return parseGroqReview(response);
        } catch (RuntimeException e) {
            AiReviewResult result = new AiReviewResult();
            result.setEnabled(true);
            result.setModel(groqModel);
            result.setStatus(groqFailureMessage(e));
            return result;
        }
    }

    private String groqFailureMessage(RuntimeException exception) {
        String details = exception.getMessage() == null ? "unknown error" : exception.getMessage();
        if (details.contains("model_not_found") || details.contains("does not exist")) {
            return "Groq model '" + groqModel
                    + "' is unavailable. Set GROQ_MODEL to a currently supported model, for example openai/gpt-oss-120b.";
        }
        return "Groq review failed: " + details;
    }

    private Map<String, Object> groqRequestPayload(String codeSnippet,
                                                   CodeStructure structure,
                                                   List<String> deterministicSmells,
                                                   List<String> deterministicPatterns) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", groqModel);
        payload.put("temperature", 0.1);
        payload.put("response_format", Map.of("type", "json_object"));
        payload.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt()),
                Map.of("role", "user", "content", userPrompt(codeSnippet, structure, deterministicSmells, deterministicPatterns))
        ));
        return payload;
    }

    private AiReviewResult reviewWithOpenAi(String codeSnippet,
                                            CodeStructure structure,
                                            List<String> deterministicSmells,
                                            List<String> deterministicPatterns) {
        try {
            String response = openAiClient.post()
                    .uri("/responses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + openAiApiKey)
                    .body(openAiRequestPayload(codeSnippet, structure, deterministicSmells, deterministicPatterns))
                    .retrieve()
                    .body(String.class);

            return parseOpenAiReview(response);
        } catch (RuntimeException e) {
            AiReviewResult result = new AiReviewResult();
            result.setEnabled(true);
            result.setModel(openAiModel);
            result.setStatus("OpenAI review failed: " + e.getMessage());
            return result;
        }
    }

    private AiReviewResult reviewWithOllama(String codeSnippet,
                                            CodeStructure structure,
                                            List<String> deterministicSmells,
                                            List<String> deterministicPatterns) {
        try {
            String response = ollamaClient.post()
                    .uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ollamaRequestPayload(codeSnippet, structure, deterministicSmells, deterministicPatterns))
                    .retrieve()
                    .body(String.class);

            return parseOllamaReview(response);
        } catch (RuntimeException e) {
            AiReviewResult result = new AiReviewResult();
            result.setEnabled(true);
            result.setModel(ollamaModel);
            result.setStatus("Ollama review failed. Start Ollama and pull the model first. Details: " + e.getMessage());
            return result;
        }
    }

    private Map<String, Object> openAiRequestPayload(String codeSnippet,
                                                     CodeStructure structure,
                                                     List<String> deterministicSmells,
                                                     List<String> deterministicPatterns) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", openAiModel);
        payload.put("reasoning", Map.of("effort", "medium"));
        payload.put("input", List.of(
                Map.of(
                        "role", "system",
                        "content", systemPrompt()
                ),
                Map.of(
                        "role", "user",
                        "content", userPrompt(codeSnippet, structure, deterministicSmells, deterministicPatterns)
                )
        ));
        payload.put("text", Map.of("format", jsonSchema()));
        return payload;
    }

    private Map<String, Object> ollamaRequestPayload(String codeSnippet,
                                                     CodeStructure structure,
                                                     List<String> deterministicSmells,
                                                     List<String> deterministicPatterns) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", ollamaModel);
        payload.put("stream", false);
        payload.put("format", "json");
        payload.put("messages", List.of(
                Map.of(
                        "role", "system",
                        "content", systemPrompt()
                ),
                Map.of(
                        "role", "user",
                        "content", userPrompt(codeSnippet, structure, deterministicSmells, deterministicPatterns)
                )
        ));
        return payload;
    }

    private String systemPrompt() {
        return """
                You are a senior competitive-programming and production-code review agent.
                Review Java code for output correctness, edge cases, complexity, DSA pattern fit, and optimization.
                Mentally trace the algorithm. Do not rely only on variable names.
                If a loop skips indices, a condition misses a boundary, or a data structure choice changes behavior, call it out.
                Return only JSON with these exact keys:
                summary, correctnessIssues, complexityNotes, dsaPatterns, optimizationPlan, improvedCode, confidence.
                The list fields must be arrays of strings. confidence must be low, medium, or high.
                """;
    }

    private String userPrompt(String codeSnippet,
                              CodeStructure structure,
                              List<String> deterministicSmells,
                              List<String> deterministicPatterns) {
        return """
                Analyze this Java code as an AI code-review agent.

                Static analyzer context:
                - Parsed successfully: %s
                - Method count: %d
                - Loop count: %d
                - Max loop nesting depth: %d
                - Static code smells: %s
                - Static DSA pattern guesses: %s
                - Static correctness warnings: %s

                Java code:
                ```java
                %s
                ```
                """.formatted(
                structure.isParsedSuccessfully(),
                structure.getMethodCount(),
                structure.getTotalLoopCount(),
                structure.getMaxLoopNestingDepth(),
                deterministicSmells,
                deterministicPatterns,
                structure.getCorrectnessWarnings(),
                codeSnippet
        );
    }

    private Map<String, Object> jsonSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("additionalProperties", false);
        schema.put("required", List.of(
                "summary",
                "correctnessIssues",
                "complexityNotes",
                "dsaPatterns",
                "optimizationPlan",
                "improvedCode",
                "confidence"
        ));
        schema.put("properties", Map.of(
                "summary", Map.of("type", "string"),
                "correctnessIssues", arrayOfStrings(),
                "complexityNotes", arrayOfStrings(),
                "dsaPatterns", arrayOfStrings(),
                "optimizationPlan", arrayOfStrings(),
                "improvedCode", Map.of("type", "string"),
                "confidence", Map.of("type", "string", "enum", List.of("low", "medium", "high"))
        ));

        return Map.of(
                "type", "json_schema",
                "name", "code_review",
                "strict", true,
                "schema", schema
        );
    }

    private Map<String, Object> arrayOfStrings() {
        return Map.of(
                "type", "array",
                "items", Map.of("type", "string")
        );
    }

    private AiReviewResult parseOpenAiReview(String response) {
        AiReviewResult result = objectMapper.convertValue(reviewJson(response), AiReviewResult.class);
        result.setEnabled(true);
        result.setModel(openAiModel);
        result.setStatus("AI review complete with OpenAI.");
        return result;
    }

    private AiReviewResult parseOllamaReview(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.path("message").path("content");
            if (!content.isTextual()) {
                throw new IllegalStateException("Ollama response did not include message content.");
            }

            AiReviewResult result = objectMapper.readValue(extractJsonObject(content.asText()), AiReviewResult.class);
            result.setEnabled(true);
            result.setModel(ollamaModel);
            result.setStatus("AI review complete with local Ollama.");
            return result;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not parse Ollama review response.", e);
        }
    }

    private AiReviewResult parseGroqReview(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.path("choices").path(0).path("message").path("content");
            if (!content.isTextual()) {
                throw new IllegalStateException("Groq response did not include message content.");
            }

            JsonNode reviewJson = objectMapper.readTree(extractJsonObject(content.asText()));
            AiReviewResult result = objectMapper.treeToValue(normalizeGroqReview(reviewJson), AiReviewResult.class);
            result.setEnabled(true);
            result.setModel(groqModel);
            result.setStatus("AI review complete with Groq.");
            return result;
        } catch (JsonProcessingException e) {
            String detail = e.getOriginalMessage() == null ? e.getMessage() : e.getOriginalMessage();
            throw new IllegalStateException("Could not parse Groq review response: " + detail, e);
        }
    }

    private String extractJsonObject(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Groq returned an empty review.");
        }

        String cleaned = content.trim()
                .replaceFirst("^```(?:json)?\\s*", "")
                .replaceFirst("\\s*```$", "")
                .trim();

        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace < 0 || lastBrace <= firstBrace) {
            throw new IllegalStateException("Groq returned no JSON review object.");
        }

        return cleaned.substring(firstBrace, lastBrace + 1);
    }

    private JsonNode normalizeGroqReview(JsonNode reviewJson) {
        if (!reviewJson.isObject()) {
            throw new IllegalStateException("Groq review must be a JSON object.");
        }

        ObjectNode normalized = (ObjectNode) reviewJson.deepCopy();
        normalizeStringField(normalized, "summary");
        normalizeStringField(normalized, "improvedCode");
        normalizeStringField(normalized, "confidence");
        normalizeArrayField(normalized, "correctnessIssues");
        normalizeArrayField(normalized, "complexityNotes");
        normalizeArrayField(normalized, "dsaPatterns");
        normalizeArrayField(normalized, "optimizationPlan");
        return normalized;
    }

    private void normalizeStringField(ObjectNode object, String fieldName) {
        JsonNode value = object.get(fieldName);
        if (value == null || value.isNull()) {
            object.put(fieldName, "");
        } else if (value.isArray()) {
            object.put(fieldName, joinArray(value, "\n"));
        } else if (!value.isTextual()) {
            object.put(fieldName, value.asText());
        }
    }

    private void normalizeArrayField(ObjectNode object, String fieldName) {
        JsonNode value = object.get(fieldName);
        if (value == null || value.isNull()) {
            object.set(fieldName, objectMapper.createArrayNode());
        } else if (value.isTextual()) {
            ArrayNode array = objectMapper.createArrayNode();
            array.add(value.asText());
            object.set(fieldName, array);
        } else if (value.isArray()) {
            ArrayNode array = objectMapper.createArrayNode();
            value.forEach(item -> array.add(item.isTextual() ? item.asText() : item.toString()));
            object.set(fieldName, array);
        }
    }

    private String joinArray(JsonNode value, String delimiter) {
        StringBuilder joined = new StringBuilder();
        value.forEach(item -> {
            if (joined.length() > 0) {
                joined.append(delimiter);
            }
            joined.append(item.isTextual() ? item.asText() : item.toString());
        });
        return joined.toString();
    }

    private JsonNode reviewJson(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            String outputText = findOutputText(root);
            return objectMapper.readTree(outputText);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not parse AI review response.", e);
        }
    }

    private String findOutputText(JsonNode node) {
        if (node == null || node.isNull()) {
            throw new IllegalStateException("AI response did not include output text.");
        }

        JsonNode directOutputText = node.get("output_text");
        if (directOutputText != null && directOutputText.isTextual()) {
            return directOutputText.asText();
        }

        if (node.isObject()) {
            for (JsonNode child : node) {
                try {
                    return findOutputText(child);
                } catch (IllegalStateException ignored) {
                    // Keep walking the response tree until a text payload is found.
                }
            }
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                try {
                    return findOutputText(child);
                } catch (IllegalStateException ignored) {
                    // Keep walking the response tree until a text payload is found.
                }
            }
        }

        throw new IllegalStateException("AI response did not include output text.");
    }
}
