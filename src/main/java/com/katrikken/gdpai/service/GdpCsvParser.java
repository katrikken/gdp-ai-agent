package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.GdpRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class GdpCsvParser extends CsvParser<Gdp, CountryYearId, GdpRepository> {

    private static final int COLUMN_COUNT = 69;
    private static final int YEAR_START_COLUMN = 4;
    private static final int YEAR_START = 1960;

    public GdpCsvParser(GdpRepository repository) {
        super(repository);
    }

    @Override
    protected Optional<Set<Gdp>> parseLine(String line, int lineNumber) {
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

            Set<Gdp> gdps = new HashSet<>();

            int year = YEAR_START;
            for (int i = YEAR_START_COLUMN; i < COLUMN_COUNT; i++) {
                String gdpValue = cleanField(cells[i]);
                if (!gdpValue.isEmpty()) {
                    try {
                        Gdp gdp = Gdp.builder().id(new CountryYearId(countryCode, year))
                                .gdp(new BigDecimal(gdpValue))
                                .build();
                        gdps.add(gdp);
                    } catch (NumberFormatException n) {
                        log.warn("Line {} skipped: Invalid GDP number for year {}. Number: {}", lineNumber, year, gdpValue);
                        return Optional.empty();
                    }
                }
                year++;
            }

            return Optional.of(gdps);

        } catch (Exception e) {
            log.error("Error parsing line {} ({}): {}", lineNumber, line, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
