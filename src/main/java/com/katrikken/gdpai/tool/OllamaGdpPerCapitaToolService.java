package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.GdpPerCapita;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OllamaGdpPerCapitaToolService {
    private final GdpPerCapitaToolService gdpPerCapitaToolService;

    @Bean
    @Description(GdpPerCapitaToolService.gdpPerCapitaByCountry_DESCRIPTION)
    public Function<DataTool.CountryCodeQuery, Map<Integer, BigDecimal>> gdpPerCapitaByCountry() {
        return (gdpPerCapitaToolService::gdpPerCapitaByCountry);
    }

    @Bean
    @Description(GdpPerCapitaToolService.gdpPerCapitaByYear_DESCRIPTION)
    public Function<DataTool.YearQuery, Map<String, BigDecimal>> gdpPerCapitaByYear() {
        return (gdpPerCapitaToolService::gdpPerCapitaByYear);
    }

    @Bean
    @Description(GdpPerCapitaToolService.gdpPerCapitaByCountryAndYearRange_DESCRIPTION)
    public Function<DataTool.CountryCodeYearRangeQuery, Map<Integer, BigDecimal>> gdpPerCapitaByCountryAndYearRange() {
        return (gdpPerCapitaToolService::gdpPerCapitaByCountryAndYearRange);
    }

    @Bean
    @Description(GdpPerCapitaToolService.gdpPerCapitaByYearRange_DESCRIPTION)
    public Function<DataTool.YearRangeQuery, List<GdpPerCapita>> gdpPerCapitaByYearRange() {
        return (gdpPerCapitaToolService::gdpPerCapitaByYearRange);
    }

}
