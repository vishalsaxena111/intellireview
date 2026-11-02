package com.javamonk.intellireview.model;

public class CodeSnippet {
    private String codeText;
    private String language = "java";

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
