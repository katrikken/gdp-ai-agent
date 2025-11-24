package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.PopulationRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class PopulationCsvParser extends CsvParser<Population, CountryYearId, PopulationRepository> {

    private static final int COLUMN_COUNT = 69;
    private static final int YEAR_START_COLUMN = 4;
    private static final int YEAR_START = 1960;

    public PopulationCsvParser(PopulationRepository repository) {
        super(repository);
    }

    @Override
    protected Optional<Set<Population>> parseLine(String line, int lineNumber) {
        // Use the Pattern to split the line based on commas outside quotes
        String[] cells = CSV_SPLIT_PATTERN.split(line);

        if (cells.length < COLUMN_COUNT) {
            log.warn("Line {} skipped: Expected at least {} fields, found {}. Raw line: {}", lineNumber, COLUMN_COUNT,
                    cells.length, line);
            return Optional.empty();
        }

        try {
            String countryCodeRaw = cleanField(cells[1]);

            String countryCode = countryCodeRaw.toUpperCase().trim();
            if (countryCode.length() != 3) {
                log.warn("Line {} skipped: Invalid Country Code length (expected 3, found {}). Code: {}",
                        lineNumber, countryCode.length(), countryCodeRaw);
                return Optional.empty();
            }

            Set<Population> populations = new HashSet<>();

            int year = YEAR_START;
            for (int i = YEAR_START_COLUMN; i < COLUMN_COUNT; i++) {
                String popValue = cleanField(cells[i]);
                if (!popValue.isEmpty()) {
                    try {
                        Population gdp = Population.builder().id(new CountryYearId(countryCode, year))
                                .population(Long.parseLong(popValue))
                                .build();
                        populations.add(gdp);
                    } catch (NumberFormatException n) {
                        log.warn("Line {} skipped: Invalid Population number for year {}. Number: {}", lineNumber, year, popValue);
                        return Optional.empty();
                    }
                }
                year++;
            }

            return Optional.of(populations);

        } catch (Exception e) {
            log.error("Error parsing line {} ({}): {}", lineNumber, line, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
