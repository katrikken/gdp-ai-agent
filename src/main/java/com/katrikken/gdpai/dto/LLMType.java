package com.katrikken.gdpai.dto;

public enum LLMType {
    OPENAI("openai"),
    OLLAMA("ollama");
//    GEMINI("gemini");
    private final String value;
    LLMType(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
