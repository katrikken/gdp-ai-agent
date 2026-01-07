package com.katrikken.gdpai.controller;

import com.katrikken.gdpai.dto.PromptDto;
import com.katrikken.gdpai.model.AiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final AiAgentService aiAgentService;
    @Value("${app.default-model:ollama}")
    private String defaultModel;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody @NotNull PromptDto prompt) {
        if (prompt.getModel() == null || prompt.getModel().isEmpty()) {
            prompt.setModel(defaultModel);
        }

        String response = aiAgentService.chat(prompt.getModel(), prompt.getPrompt());
        return ResponseEntity.ok(response);
    }
}
