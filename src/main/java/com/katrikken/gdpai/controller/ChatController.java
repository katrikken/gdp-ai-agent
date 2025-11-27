package com.katrikken.gdpai.controller;

import com.katrikken.gdpai.model.AiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final AiAgentService aiAgentService;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody @NotNull String prompt) {
        String response = aiAgentService.chat(prompt);
        return ResponseEntity.ok(response);
    }
}
