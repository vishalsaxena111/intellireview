🚀 Java Monk – IntelliReview
Intelligent Code Review Assistant for Java

IntelliReview is an AI-inspired static code analysis tool that automatically reviews Java code and provides insights such as:

Time Complexity
Space Complexity
Code Smell Detection
Structural Code Analysis

The system uses AST parsing via JavaParser to analyze Java code programmatically.

📊 Features

✔ Java AST Parsing
✔ Automatic Complexity Estimation
✔ Code Smell Detection
✔ Structural Metrics Extraction
✔ Rule-Based Code Intelligence

🏗️ Architecture

                 +------------------+
                 |  REST Controller |
                 +------------------+
                          |
                          v
                 +------------------+
                 | CodeReviewService|
                 +------------------+
                          |
        ----------------------------------------
        |                 |                      |
        v                 v                      v
+----------------+  +----------------+  +---------------------+
|CodeParserService| |ComplexityService| |CodeSmellService     |
+----------------+  +----------------+  +---------------------+
        |                 |                      |
        ----------------------------------------
                          |
                          v
                 +------------------+
                 |  Review Result   |
                 +------------------+

🛠️ Tech Stack

Java 17
Spring Boot
JavaParser
Maven
PostgreSQL

🔮 Future Roadmap

Cyclomatic Complexity
GitHub PR Integration
ML-based code smell detection
Code quality scoring
AI-powered recommendations

👨‍💻 Author

Vishal Saxena
Senior Java Backend Engineer

LinkedIn
linkedin.com/in/vishal-saxena17
