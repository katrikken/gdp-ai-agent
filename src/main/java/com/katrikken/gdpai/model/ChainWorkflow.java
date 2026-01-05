package com.katrikken.gdpai.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class ChainWorkflow {

    private final String QUESTION = "{question}";
    private final String DATA = "{data}";
    @Value("${app.system-message.plan}")
    private String SYSTEM_MESSAGE_PLAN;
    @Value("${app.system-message.tools}")
    private String SYSTEM_MESSAGE_TOOLS;
    @Value("${app.system-message.analysis}")
    private String SYSTEM_MESSAGE_ANALYSIS;

    public String chain(String userInput, ChatClient chatClient) {

        log.info("Received user query: {}", userInput);

        String input = SYSTEM_MESSAGE_PLAN.replace(QUESTION, userInput);

        String dataRequest = chatClient.prompt(input).call().content();

        log.info("Need following data to respond: {}", dataRequest);

        if (dataRequest == null || dataRequest.isEmpty()) {
            return "ERROR. Could not determine needed data";
        }

        String data = chatClient.prompt(SYSTEM_MESSAGE_TOOLS.replace(QUESTION, dataRequest))
//                    .tools(new MathematicalTool(),
//                            countryToolService,
//                            gdpToolService,
//                            populationToolService,
//                            gdpPerCapitaToolService)
                .toolNames("gdpPerCapitaByCountry", "gdpPerCapitaByYear",
                        "gdpPerCapitaByYearRange", "gdpPerCapitaTrendForCountry",

                        "populationByCountryYear", "populationByCountry", "populationByYear",
                        "populationBetweenYears", "populationTrendForCountry", "insertPopulation",

                        "gdpByCountryYear", "gdpByCountry", "gdpByYear",
                        "gdpBetweenYears", "gdpTrendForCountry", "insertGdp",

                        "countryNameToCountryCode",

                        "getCurrentYear", "operateOnBigDecimals"
                )
                .call().content();

        if (data == null || data.isEmpty()) {
            return "ERROR. Could not find data to answer the question";
        }

        log.info("Obtained data: {}", data);

        String response = chatClient
                .prompt(SYSTEM_MESSAGE_ANALYSIS.replace(QUESTION, userInput)
                        .replace(DATA, data))
                .call().content();

        return response;
    }
}