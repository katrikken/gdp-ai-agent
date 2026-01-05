package com.katrikken.gdpai.tool.ollama;

import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.tool.DataTool;
import com.katrikken.gdpai.tool.PopulationToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OllamaPopulationToolService {
    private final PopulationToolService populationToolService;

    @Bean
    @Description(PopulationToolService.INSERT_POPULATION_DESCRIPTION)
    public Function<Population, String> insertPopulation() {
        return populationToolService::insertPopulationTool;
    }

    @Bean
    @Description(PopulationToolService.GET_POPULATION_BY_COUNTRY_YEAR_DESCRIPTION)
    public Function<DataTool.CountryCodeYearQuery, String> populationByCountryYear() {
        return populationToolService::populationByCountryCodeYearTool;
    }

    @Bean
    @Description(PopulationToolService.GET_POPULATION_BY_COUNTRY_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> populationByCountry() {
        return populationToolService::populationByCountryCodeTool;
    }

    @Bean
    @Description(PopulationToolService.GET_POPULATION_BY_YEAR_DESCRIPTION)
    public Function<DataTool.YearQuery, String> populationByYear() {
        return populationToolService::populationByYearTool;
    }

    @Bean
    @Description(PopulationToolService.POPULATION_BETWEEN_YEARS_DESCRIPTION)
    public Function<DataTool.YearRangeQuery, String> populationBetweenYears() {
        return populationToolService::populationBetweenYearTool;
    }

    @Bean
    @Description(PopulationToolService.POPULATION_TREND_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, String> populationTrendForCountryTool() {
        return populationToolService::populationTrendForCountryTool;
    }
}