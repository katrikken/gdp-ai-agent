package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Log4j2
@RequiredArgsConstructor
public class CountryCsvParser {

    // Pattern to safely split CSV lines while respecting quoted fields.
    // This simple pattern looks for a comma that is NOT inside double quotes.
    // WARNING: This is a fragile, custom solution. A dedicated CSV library (like OpenCSV)
    // is highly recommended for production code to handle all edge cases (escaped quotes, etc.).
    private static final Pattern CSV_SPLIT_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    // Normalized income group values
    private static final List<String> INCOME_GROUPS = Arrays.asList(
            "High income", "Upper middle income", "Lower middle income", "Low income", ""
    );
    private final CountryRepository countryRepository;


    /**
     * Load a CSV file, skip the header, and process each data line.
     *
     * @param filePath The local path to the CSV file.
     * @return A list of successfully parsed Country objects.
     */
    public List<Country> loadAndSaveCsvData(String filePath) {
        List<Country> countries = new ArrayList<>();
        log.info("Starting CSV file processing from path: {}", filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<CsvRow> rawRecords = readCsvRows(br);
            if (rawRecords.isEmpty()) {
                log.warn("CSV file is empty.");
                return countries;
            }

            // 2. Iterate over the records (starting from index 1 to skip header)
            for (CsvRow csvRecord : rawRecords) {
                Optional<Country> country = parseLine(csvRecord.row, csvRecord.rowNumber);

                country.ifPresent(co -> {
                    countries.add(co);
                    countryRepository.save(co);
                });
            }


            log.info("Finished processing file. Successfully parsed and saved {} countries.", countries.size());

        } catch (IOException e) {
            log.error("Error reading CSV file at path {}: {}", filePath, e.getMessage());
        }

        return countries;
    }

    /**
     * Reads the CSV file character-by-character using a state machine to correctly
     * identify logical record boundaries, even when newlines occur inside quoted fields.
     *
     * @param br The BufferedReader of the CSV file.
     * @return A list of CsvRecord objects containing the raw line and its logical record number.
     * @throws IOException if a file reading error occurs.
     */
    private List<CsvRow> readCsvRows(BufferedReader br) throws IOException {
        List<CsvRow> records = new ArrayList<>();

        String header = br.readLine();
        if (header != null) {
            log.info("Skipped header line: {}", header);
        }

        // --- State Machine Variables ---
        boolean inQuotedField = false;
        int charRead;
        int recordNumber = 1; // Start counting from 1
        StringBuilder currentRow = new StringBuilder();

        while ((charRead = br.read()) != -1) {
            char c = (char) charRead;

            if (c == '"') {
                inQuotedField = !inQuotedField;
            }

            if (c == '\n') {
                if (inQuotedField) {
                    // Newline inside a quote: append it and continue reading the record.
                    currentRow.append(c);
                } else {
                    String rowToParse = currentRow.toString().trim();

                    if (!rowToParse.isEmpty()) {
                        records.add(new CsvRow(rowToParse, recordNumber));
                        recordNumber++;
                    }

                    // Reset for the next record
                    currentRow = new StringBuilder();
                }
            } else if (c != '\r') {
                currentRow.append(c);
            }
        }

        // 4. Handle any remaining content after EOF (if the file doesn't end with a newline)
        String finalRecord = currentRow.toString().trim();
        if (!finalRecord.isEmpty()) {
            log.warn("File ended without a final newline, processing last record.");
            records.add(new CsvRow(finalRecord, recordNumber));
        }

        return records;
    }

    /**
     * Private method responsible for parsing a single, raw CSV line into a Country instance.
     *
     * @param line       The single raw line string from the CSV.
     * @param lineNumber The line number for logging purposes.
     * @return An Optional containing the parsed Country object, or empty if validation failed.
     */
    private Optional<Country> parseLine(String line, int lineNumber) {
        // Use the Pattern to split the line based on commas outside quotes
        String[] cells = CSV_SPLIT_PATTERN.split(line);

        // The expected structure is 5 fields
        if (cells.length < 5) {
            log.warn("Line {} skipped: Expected at least 5 fields, found {}. Raw line: {}", lineNumber, cells.length, line);
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

            return Optional.of(country);

        } catch (Exception e) {
            log.error("Error parsing line {} ({}): {}", lineNumber, line, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Cleans a field by trimming whitespace and removing surrounding double quotes.
     */
    private String cleanField(String field) {
        String cleaned = field.trim();
        // Remove surrounding quotes if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() > 1) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.trim();
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

    @RequiredArgsConstructor
    private class CsvRow {
        private final String row;
        private final int rowNumber;
    }
}