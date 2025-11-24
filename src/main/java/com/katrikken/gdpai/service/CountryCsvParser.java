package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class CountryCsvParser extends CsvParser<Country, String, CountryRepository> {

    // Normalized income group values
    private static final List<String> INCOME_GROUPS = Arrays.asList(
            "High income", "Upper middle income", "Lower middle income", "Low income", ""
    );
    private static int COLUMN_COUNT = 5;

    public CountryCsvParser(CountryRepository repository) {
        super(repository);
    }

    /**
     * Private method responsible for parsing a single, raw CSV line into a Country instance.
     *
     * @param line       The single raw line string from the CSV.
     * @param lineNumber The line number for logging purposes.
     * @return An Optional containing the parsed Country object, or empty if validation failed.
     */
    @Override
    protected Optional<Set<Country>> parseLine(String line, int lineNumber) {
        // Use the Pattern to split the line based on commas outside quotes
        String[] cells = CSV_SPLIT_PATTERN.split(line);

        if (cells.length < COLUMN_COUNT) {
            log.warn("Line {} skipped: Expected at least {} fields, found {}. Raw line: {}", lineNumber, COLUMN_COUNT,
                    cells.length, line);
            return Optional.empty();
        }

        try {
            // Helper function to clean quotes and trim whitespace from fields
            String countryCodeRaw = cleanField(cells[0]);
            String region = cleanField(cells[1]);
            String incomeGroupRaw = cleanField(cells[2]);
            String specialNotes = cleanField(cells[3]);
            String countryName = cleanField(cells[4]);

            // --- 1. Country Code Validation and Normalization (Upper Case, 3 Letters) ---
            String countryCode = countryCodeRaw.toUpperCase().trim();
            if (countryCode.length() != 3) {
                log.warn("Line {} skipped: Invalid Country Code length (expected 3, found {}). Code: {}", lineNumber, countryCode.length(), countryCodeRaw);
                return Optional.empty();
            }

            String normalizedIncomeGroup = normalizeIncomeGroup(incomeGroupRaw);
            if (normalizedIncomeGroup == null) {
                log.warn("Line {} skipped: Income Group '{}' could not be normalized to required values.", lineNumber, incomeGroupRaw);
                return Optional.empty();
            }

            Country country = Country.builder()
                    .countryCode(countryCode)
                    .region(region)
                    .incomeGroup(normalizedIncomeGroup)
                    .specialNotes(specialNotes)
                    .name(countryName)
                    .build();

            return Optional.of(Set.of(country));

        } catch (Exception e) {
            log.error("Error parsing line {} ({}): {}", lineNumber, line, e.getMessage(), e);
            return Optional.empty();
        }
    }


    /**
     * Normalizes the raw income group string to one of the predefined list values.
     * It handles case insensitivity and extra spaces.
     *
     * @return The normalized income group string, or null if no match is found.
     */
    private String normalizeIncomeGroup(String rawGroup) {
        if (rawGroup == null) return null;

        // Clean up the raw string for comparison
        String comparableRaw = rawGroup.toLowerCase().replaceAll("\\s+", " ").trim();

        for (String validGroup : INCOME_GROUPS) {
            if (validGroup.toLowerCase().equals(comparableRaw)) {
                return validGroup; // Return the correctly cased, normalized version
            }
        }
        return null;
    }

}