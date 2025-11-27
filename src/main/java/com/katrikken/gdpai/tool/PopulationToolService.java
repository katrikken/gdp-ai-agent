package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.PopulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
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

    @Tool(description = "Returns all known Population data (String).")
    public Function<Void, List<Population>> getAllPopulationTool() {
        log.info("getAllPopulationTool called");
        return (Void) -> repository.findAll();
    }

    @Tool(description = "Inserts Population data (Population).")
    public Function<Population, Population> insertPopulationTool() {
        return (Population population) -> {
            log.info("insertPopulationTool called with population: " + population);
            return repository.save(population);
        };
    }

    @Tool(description = "Takes a CountryCodeYearQuery (countryCode, year) and returns Population data (String).")
    public Function<CountryCodeYearQuery, String> populationByCountryCodeYearTool() {
        return (CountryCodeYearQuery query) -> {
            try {
                log.info("populationByCountryCodeYearTool called with query: " + query);
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

    @Tool(description = "Takes a Country code (String) and returns Population data list sorted by year (List).")
    public Function<CountryCodeQuery, List<Population>> populationByCountryCodeTool() {
        return (CountryCodeQuery query) -> {
            log.info("populationByCountryCodeTool called with query: " + query);
            return repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
        };
    }

    @Tool(description = "Takes a YearQuery (year) and returns Population data list sorted by Country Code (List).")
    public Function<YearQuery, List<Population>> populationByYearTool() {
        return (YearQuery query) -> {
            log.info("populationByYearTool called with query: " + query);
            return repository.findByIdDataYearOrderByIdCountryCode(query.year());
        };
    }

    @Tool(description = "Takes a list of Population class (List) and returns the list sorted by Population value (List).")
    public Function<List<Population>, List<Population>> populationsSortByPopulationValueTool() {
        log.info("populationsSortByPopulationValueTool called");
        return (List<Population> populations) -> populations.stream()
                .sorted(Comparator.comparing(Population::getPopulation)).toList();
    }

    @Tool(description = "Takes a YearRangeQuery (startYear, endYear) and returns Population data list sorted by Country Code (List).")
    public Function<YearRangeQuery, List<Population>> populationBetweenYearTool() {
        return (YearRangeQuery interval) -> {
            log.info("populationBetweenYearTool called with interval: " + interval);
            return repository
                    .findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
        };
    }
}