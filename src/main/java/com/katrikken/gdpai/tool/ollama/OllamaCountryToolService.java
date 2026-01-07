package com.katrikken.gdpai.tool.ollama;

import com.katrikken.gdpai.tool.CountryToolService;
import com.katrikken.gdpai.tool.DataTool;
import com.katrikken.gdpai.tool.GdpPerCapitaToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OllamaCountryToolService {
    private final CountryToolService countryToolService;

    @Bean
    @Description(GdpPerCapitaToolService.GDP_PER_CAPITA_BY_COUNTRY_DESCRIPTION)
    public Function<DataTool.CountryQuery, String> countryNameToCountryCode() {
        return (countryToolService::countryNameToCountryCodeTool);
    }
}
