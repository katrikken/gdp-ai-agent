package com.katrikken.gdpai.model;

import com.katrikken.gdpai.dto.LLMType;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@Log4j2
public class AiAgentService {

    private final ChatClient ollamaChatClient;
    private final ChatClient openAIChatClient;
    private final ChainWorkflow chainWorkflow;

    public AiAgentService(OpenAiChatModel openAiChatModel,
                          OllamaChatModel ollamaChatModel,
                          ChatMemory chatMemory,
                          ChainWorkflow chainWorkflow) {
        log.debug("In AiAgentService initialization for session");
        this.openAIChatClient = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();

        this.ollamaChatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.chainWorkflow = chainWorkflow;
    }

    public String chat(String model, String prompt) {
        try {
            ChatClient chatClient = getChatModel(LLMType.valueOf(model.toUpperCase()));
            return chainWorkflow.chain(prompt, chatClient);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Could not process prompt, got the following error: " + e.getMessage();
        }
    }

    private ChatClient getChatModel(LLMType llmName) {
        return switch (llmName) {
            case OPENAI -> openAIChatClient;
            case OLLAMA -> ollamaChatClient;
        };
    }
}
