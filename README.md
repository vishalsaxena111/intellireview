# IntelliReview

IntelliReview is a Spring Boot application that reviews Java code using JavaParser-based static analysis and optional AI assistance through Groq, OpenAI, or local Ollama.

## What it provides

- Java AST parsing for full classes and code fragments.
- Syntax errors with line and column information.
- Class, method, field, loop, conditional, switch, and return metrics.
- Estimated time and space complexity.
- Recursion and loop-index correctness warnings.
- Code-smell detection for nesting, recursion, loops, and return paths.
- DSA pattern detection for hashing, two pointers, sliding windows, binary search, recursion, queues, stacks, sorting, and dynamic programming.
- Optimization suggestions based on the detected structure.
- AI review with summary, correctness issues, complexity notes, DSA analysis, optimization advice, improved code, and confidence.
- Browser UI with a Java editor and review dashboard.

When AI review succeeds, the AI results are shown as the primary review and duplicate deterministic sections are hidden. Complexity, loop, and method metrics remain visible. Deterministic analysis is used as a fallback when AI is disabled or unavailable.

## Technology

- Java 17
- Spring Boot 3.5.5
- Spring Web
- Maven
- JavaParser 3.25.4
- Jackson
- HTML, CSS, and JavaScript frontend

## Project structure

```text
src/main/java/com/javamonk/intellireview/
├── IntellireviewApplication.java
├── controller/
│   ├── CodeReviewController.java
│   └── HealthCheckController.java
├── model/
│   ├── AiReviewResult.java
│   ├── CodeAnalysisResult.java
│   ├── CodeSnippet.java
│   └── CodeStructure.java
└── service/
    ├── AiCodeReviewService.java
    ├── CodeParserService.java
    ├── CodeReviewService.java
    ├── CodeSmellClassifierService.java
    ├── ComplexityEstimatorService.java
    ├── DsaPatternDetectorService.java
    └── OptimizationSuggestionService.java

src/main/resources/static/
├── index.html
├── app.js
└── styles.css
```

## Run locally

Requirements: JDK 17 or newer. Maven is optional because the Maven wrapper is included.

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`.

## Groq AI setup

The API key must be supplied through the environment. Do not commit it to Git.

```powershell
$env:AI_PROVIDER="groq"
$env:GROQ_API_KEY="your_groq_api_key"
$env:GROQ_MODEL="openai/gpt-oss-120b"

.\mvnw.cmd spring-boot:run
```

The default Groq endpoint is:

```text
https://api.groq.com/openai/v1
```

Configuration is defined in `src/main/resources/application.properties`:

```properties
ai.provider=${AI_PROVIDER:openai}
groq.api-key=${GROQ_API_KEY:}
groq.model=${GROQ_MODEL:openai/gpt-oss-120b}
groq.base-url=${GROQ_BASE_URL:https://api.groq.com/openai/v1}
```

Use a currently supported model from the Groq model list. The application reports a readable message when the configured model is unavailable.

## Other AI providers

### OpenAI

```powershell
$env:AI_PROVIDER="openai"
$env:OPENAI_API_KEY="your_openai_api_key"
$env:OPENAI_MODEL="gpt-5.5"
.\mvnw.cmd spring-boot:run
```

### Ollama

```powershell
$env:AI_PROVIDER="ollama"
$env:OLLAMA_MODEL="qwen2.5-coder:7b"
.\mvnw.cmd spring-boot:run
```

Ollama must be running locally with the selected model downloaded.

## REST API

### Analyze Java code

```http
POST /api/v1/code-review/analyze
Content-Type: application/json
```

Example request:

```json
{
  "language": "java",
  "codeText": "public class Example { public void run() { } }"
}
```

Java is currently the only supported language. The response includes `parsedSuccessfully`, `summary`, `timeComplexity`, `spaceComplexity`, `codeSmells`, `correctnessWarnings`, `dsaPatterns`, `optimizationSuggestions`, `structure`, and `aiReview`.

### Health check

```http
GET /health/check
```

## Error handling

Invalid Java syntax is reported as a concise diagnostic, for example:

```text
Syntax error at line 8, column 45: Parse error. Found "int", expected ...
```

JavaParser stack traces are not shown in the browser. AI responses are normalized to handle Markdown-wrapped JSON, extra text, arrays returned for string fields, strings returned for list fields, and missing optional fields.

## Limitations

- Complexity values are structural estimates, not formal proofs.
- The application does not execute submitted code.
- Runtime exceptions cannot be fully detected statically.
- Full Java compiler diagnostics requiring project dependencies are not implemented yet.

## Tests

```powershell
.\mvnw.cmd test
```

Tests cover application startup, valid analysis, request validation, loop-skipping warnings, and concise syntax-error diagnostics.

## Future improvements

- Add full compiler diagnostics with a controlled classpath.
- Add safe sandboxed runtime testing.
- Improve control-flow and data-flow complexity analysis.
- Support additional programming languages.
- Add focused unit tests for each analyzer service.

## Author

Vishal Saxena
