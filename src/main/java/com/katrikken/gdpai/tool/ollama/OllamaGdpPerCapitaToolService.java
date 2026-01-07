package com.katrikken.gdpai.tool.ollama;

import com.katrikken.gdpai.tool.DataTool;
import com.katrikken.gdpai.tool.GdpPerCapitaToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OllamaGdpPerCapitaToolService {
    private final GdpPerCapitaToolService gdpPerCapitaToolService;

    @Bean
    @Description(GdpPerCapitaToolService.GDP_PER_CAPITA_BY_COUNTRY_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> gdpPerCapitaByCountry() {
        return (gdpPerCapitaToolService::gdpPerCapitaByCountry);
    }

    @Bean
    @Description(GdpPerCapitaToolService.GDP_PER_CAPITA_BY_YEAR_DESCRIPTION)
    public Function<DataTool.YearQuery, String> gdpPerCapitaByYear() {
        return (gdpPerCapitaToolService::gdpPerCapitaByYear);
    }

    @Bean
    @Description(GdpPerCapitaToolService.GDP_PER_CAPITA_BY_YEAR_RANGE_DESCRIPTION)
    public Function<DataTool.YearRangeQuery, String> gdpPerCapitaByYearRange() {
        return (gdpPerCapitaToolService::gdpPerCapitaByYearRange);
    }

    @Bean
    @Description(GdpPerCapitaToolService.GDP_PER_CAPITA_TREND_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> gdpPerCapitaTrendForCountry() {
        return (gdpPerCapitaToolService::gdpPerCapitaTrendForCountryTool);
    }
}
