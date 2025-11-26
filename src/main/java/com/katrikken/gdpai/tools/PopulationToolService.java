package com.katrikken.gdpai.tools;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.PopulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class PopulationToolService extends DataTool {

    private final PopulationRepository repository;

    /**
     * AI Tool: Retrieves all Population values.
     *
     * @return a Function
     */
    @Description("Returns all known Population data (String).")
    @Bean
    public Function<Void, List<Population>> getAllPopulationTool() {
        return (Void) -> repository.findAll();
    }


    /**
     * AI Tool: Inserts Population value.
     *
     * @return a Function
     */
    @Description("Inserts Population data (Population).")
    @Bean
    public Function<Population, Population> insertPopulationTool() {
        return repository::save;
    }

    /**
     * AI Tool: Retrieves the population value for a specific country and year.
     *
     * @return a Function
     */
    @Description("Takes a CountryCodeYearQuery (countryCode, year) and returns Population data (String).")
    @Bean
    public Function<CountryCodeYearQuery, String> populationByCountryCodeYearTool() {
        return (CountryCodeYearQuery query) -> {
            try {
                Optional<Population> population = repository.findById(new CountryYearId(query.countryCode(), query.year()));
                if (population.isPresent()) {
                    return String.valueOf(population.get().getPopulation());
                } else {
                    return String.format("Population data are not available for country code %s in year %d",
                            query.countryCode(), query.year());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return String.format("Error: Population data not found for country code %s in year %d.",
                        query.countryCode(), query.year());
            }
        };
    }

    /**
     * AI Tool: Retrieves all data on the Population for a specific country.
     *
     * @return a Function
     */
    @Description("Takes a Country code (String) and returns Population data list sorted by year (List).")
    @Bean
    public Function<CountryCodeQuery, List<Population>> populationByCountryCodeTool() {
        return (CountryCodeQuery query) -> repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
    }

    /**
     * AI Tool: Retrieves all data on the Population for a specific year.
     *
     * @return a Function
     */
    @Description("Takes a YearQuery (year) and returns Population data list sorted by Country Code (List).")
    @Bean
    public Function<YearQuery, List<Population>> populationByYearTool() {
        return (YearQuery query) -> repository.findByIdDataYearOrderByIdCountryCode(query.year());
    }

    /**
     * AI Tool: Sorts data on Population by value from lowest to highest.
     *
     * @return a Function that takes a list of Population class (List) and returns the list sorted by Population value (List).
     */
    @Description("Takes a list of Population class (List) and returns the list sorted by Population value (List).")
    @Bean
    public Function<List<Population>, List<Population>> populationsSortByPopulationValueTool() {
        return (List<Population> populations) -> populations.stream()
                .sorted(Comparator.comparing(Population::getPopulation)).toList();
    }

    /**
     * AI Tool: Retrieves all data on the Population for a specific year period.
     *
     * @return a Function
     */
    @Description("Takes a YearRangeQuery (startYear, endYear) and returns Population data list sorted by Country Code (List).")
    @Bean
    public Function<YearRangeQuery, List<Population>> populationBetweenYearTool() {
        return (YearRangeQuery interval) -> repository
                .findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
    }
}