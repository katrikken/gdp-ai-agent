package com.katrikken.gdpai.model;

import com.katrikken.gdpai.tool.CountryToolService;
import com.katrikken.gdpai.tool.GdpPerCapitaToolService;
import com.katrikken.gdpai.tool.GdpToolService;
import com.katrikken.gdpai.tool.MathematicalTool;
import com.katrikken.gdpai.tool.PopulationToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class AiAgentService {
    private final ChatClient chatClient;
    private final CountryToolService countryToolService;
    private final GdpToolService gdpToolService;
    private final PopulationToolService populationToolService;
    private final GdpPerCapitaToolService gdpPerCapitaToolService;

    public AiAgentService(ChatModel chatModel, ChatMemory chatMemory, CountryToolService countryToolService, GdpToolService gdpToolService, PopulationToolService populationToolService, GdpPerCapitaToolService gdpPerCapitaToolService) {
        this.countryToolService = countryToolService;
        this.gdpToolService = gdpToolService;
        this.populationToolService = populationToolService;
        this.gdpPerCapitaToolService = gdpPerCapitaToolService;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String chat(String prompt) {
        return chatClient.prompt()
                .user(userMessage -> userMessage.text(prompt))
                .tools(new MathematicalTool(),
                        countryToolService,
                        gdpToolService,
                        populationToolService,
                        gdpPerCapitaToolService)
                .call()
                .content();
    }
}
