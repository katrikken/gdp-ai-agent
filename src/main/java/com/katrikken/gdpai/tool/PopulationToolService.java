package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.PopulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PopulationToolService extends DataTool {
    public static final String INSERT_POPULATION_DESCRIPTION =
            "Insert Population data. Input: Population entity. Output: single formatted string: countryCode, year: population.";

    public static final String GET_POPULATION_BY_COUNTRY_YEAR_DESCRIPTION =
            "Retrieve a single Population record for a specific country code and year. Input: CountryCodeYearQuery. " +
                    "Output: single formatted string: countryCode, year: population or an explanatory message if not found.";

    public static final String GET_POPULATION_BY_COUNTRY_DESCRIPTION =
            "Retrieve all Population records for a given country sorted by year. Input: CountryCodeQuery. " +
                    "Output: multi-line string with entries formatted as countryCode, year: population.";

    public static final String GET_POPULATION_BY_YEAR_DESCRIPTION =
            "Retrieve all Population records for a specific year sorted by country code. Input: YearQuery. " +
                    "Output: multi-line string with entries formatted as countryCode, year: population.";

    public static final String POPULATION_BETWEEN_YEARS_DESCRIPTION =
            "Retrieve Population records between two years inclusive sorted by country code. Input: YearRangeQuery. " +
                    "Output: multi-line string with entries formatted as countryCode, year: population.";

    public static final String POPULATION_TREND_DESCRIPTION =
            "Return Population historical trend for a country. Input CountryCodeQuery. " +
                    "Outputs a multi-line string starting with Population development for the country including growth percentage.";


    private final PopulationRepository repository;

    private String formatPopulation(Population p) {
        if (p == null || p.getId() == null) {
            return "null record";
        }
        CountryYearId id = p.getId();
        String value = Optional.ofNullable(p.getPopulation()).map(Object::toString).orElse("null");
        return String.format("%s, %d: %s", id.getCountryCode(), id.getDataYear(), value);
    }

    @Tool(description = INSERT_POPULATION_DESCRIPTION)
    public String insertPopulationTool(Population population) {
        log.info("insertPopulationTool called with population: {}", population);
        try {
            Population saved = repository.save(population);
            return formatPopulation(saved);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Error: could not insert population data.";
        }
    }


    @Tool(description = GET_POPULATION_BY_COUNTRY_YEAR_DESCRIPTION)
    public String populationByCountryCodeYearTool(CountryCodeYearQuery query) {
        try {
            log.info("populationByCountryCodeYearTool called with query: {}", query);
            Optional<Population> population = repository.findById(new CountryYearId(query.countryCode(), query.year()));
            if (population.isPresent()) {
                return formatPopulation(population.get());
            } else {
                return String.format("Population data are not available for country code %s in year %d",
                        query.countryCode(), query.year());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: Population data not found for country code %s in year %d.",
                    query.countryCode(), query.year());
        }
    }

    @Tool(description = GET_POPULATION_BY_COUNTRY_DESCRIPTION)
    public String populationByCountryCodeTool(CountryCodeQuery query) {
        log.info("populationByCountryCodeTool called with query: {}", query);
        try {
            List<Population> results = repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
            return results.stream().map(this::formatPopulation).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: Population data not found for country code %s.", query.countryCode());
        }
    }

    @Tool(description = GET_POPULATION_BY_YEAR_DESCRIPTION)
    public String populationByYearTool(YearQuery query) {
        log.info("populationByYearTool called with query: {}", query);
        try {
            List<Population> results = repository.findByIdDataYearOrderByIdCountryCode(query.year());
            return results.stream().map(this::formatPopulation).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: Population data not found for year %d.", query.year());
        }
    }

    @Tool(description = POPULATION_BETWEEN_YEARS_DESCRIPTION)
    public String populationBetweenYearTool(YearRangeQuery interval) {
        log.info("populationBetweenYearTool called with interval: {}", interval);
        try {
            List<Population> results = repository.findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
            return results.stream().map(this::formatPopulation).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: Population data not found between years %d and %d.", interval.startYear(), interval.endYear());
        }
    }

    @Tool(description = POPULATION_TREND_DESCRIPTION)
    public String populationTrendForCountryTool(CountryCodeQuery countryCode) {
        log.info("populationTrendForCountryTool called with CountryCodeQuery {}", countryCode);
        List<Population> results = repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
        String result = buildTrendForCountry(
                "Population development for the country",
                results,
                Population::getId,
                p -> {
                    Long val = p.getPopulation();
                    return val == null ? null : new BigDecimal(val);
                },
                countryCode.countryCode()
        );
        log.debug("Generated Population trend string:\n{}", result);
        return result;
    }
}