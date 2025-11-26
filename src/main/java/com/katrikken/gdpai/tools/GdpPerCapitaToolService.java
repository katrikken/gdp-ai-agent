package com.katrikken.gdpai.tools;

import com.katrikken.gdpai.entity.GdpPerCapita;
import com.katrikken.gdpai.repository.GdpPerCapitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

/**
 * Component that exposes methods from the GdpPerCapitaRepository as "Tools"
 * for an AI agent using the Spring AI Function Calling mechanism.
 * <p>
 * Each public method here acts as a callable function for the LLM.
 */
@Service
@RequiredArgsConstructor
public class GdpPerCapitaToolService extends DataTool {

    private final GdpPerCapitaRepository repository;

    // --- Input Records for Function Parameters ---

    /**
     * Tool to retrieve the GDP per capita data for all available years for a specific country.
     *
     * @return A Java Function that takes a CountryCodeQuery record and returns a list of GdpPerCapita entities.
     */
    @Bean
    @Description("Gets the GDP per capita values for a single country across all years available in the database.")
    public Function<CountryCodeQuery, List<GdpPerCapita>> gdpPerCapitaByCountry() {
        return (query) -> repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
    }

    /**
     * Tool to retrieve the GDP per capita data for all countries in a specific year.
     *
     * @return A Java Function that takes a YearQuery record and returns a list of GdpPerCapita entities.
     */
    @Bean
    @Description("Gets the GDP per capita values for all countries for a specific year.")
    public Function<YearQuery, List<GdpPerCapita>> gdpPerCapitaByYear() {
        return (query) -> repository.findByIdDataYearOrderByIdCountryCode(query.year());
    }

    /**
     * Tool to retrieve the GDP per capita data for all countries within a specified year range.
     *
     * @return A Java Function that takes a YearRangeQuery record and returns a list of GdpPerCapita entities.
     */
    @Bean
    @Description("Gets the GDP per capita values for all countries within a given start year and end year range (inclusive).")
    public Function<YearRangeQuery, List<GdpPerCapita>> gdpPerCapitaByYearRange() {
        return (query) -> repository.findByIdDataYearBetweenOrderByIdCountryCode(query.startYear(), query.endYear());
    }

}
