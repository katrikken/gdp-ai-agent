package com.katrikken.gdpai.tool.ollama;

import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.tool.DataTool;
import com.katrikken.gdpai.tool.GdpToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OllamaGdpToolService {
    private final GdpToolService gdpToolService;

    @Bean
    @Description(GdpToolService.INSERT_GDP_DESCRIPTION)
    public Function<Gdp, String> insertGdp() {
        return (gdpToolService::insertGdpTool);
    }

    @Bean
    @Description(GdpToolService.GET_GDP_BY_COUNTRY_YEAR_DESCRIPTION)
    public Function<DataTool.CountryCodeYearQuery, String> gdpByCountryYear() {
        return (gdpToolService::gdpByCountryCodeYearTool);
    }

    @Bean
    @Description(GdpToolService.GET_GDP_BY_COUNTRY_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> gdpByCountry() {
        return (gdpToolService::gdpByCountryCodeTool);
    }

    @Bean
    @Description(GdpToolService.GET_GDP_BY_YEAR_DESCRIPTION)
    public Function<DataTool.YearQuery, String> gdpByYear() {
        return (gdpToolService::gdpByYearTool);
    }

    @Bean
    @Description(GdpToolService.GET_GDP_BETWEEN_YEARS_DESCRIPTION)
    public Function<DataTool.YearRangeQuery, String> gdpBetweenYears() {
        return (gdpToolService::gdpBetweenYearTool);
    }

    @Bean
    @Description(GdpToolService.GDP_TREND_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> gdpTrendForCountryTool() {
        return (gdpToolService::gdpTrendForCountryTool);
    }
}
