package com.katrikken.gdpai.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

@Log4j2
@RequiredArgsConstructor
public abstract class CsvParser<C, I, R extends JpaRepository<C, I>> {

    // Pattern to safely split CSV lines while respecting quoted fields.
    // This simple pattern looks for a comma that is NOT inside double quotes.
    // WARNING: This is a fragile, custom solution. A dedicated CSV library (like OpenCSV)
    // is highly recommended for production code to handle all edge cases (escaped quotes, etc.).
    protected static final Pattern CSV_SPLIT_PATTERN = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    private final R repository;

    /**
     * Load a CSV file, skip the header, and process each data line.
     *
     * @param filePath The local path to the CSV file.
     * @return A list of successfully parsed Country objects.
     */
    @Transactional
    public List<C> loadAndSaveCsvData(String filePath) {
        List<C> dataList = new ArrayList<>();
        log.info("Starting CSV file processing from path: {}", filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<CsvRow> rawRecords = readCsvRows(br);
            if (rawRecords.isEmpty()) {
                log.warn("CSV file is empty.");
                return dataList;
            }

            for (CsvRow csvRecord : rawRecords) {
                Optional<Set<C>> dataEntities = parseLine(csvRecord.getRow(), csvRecord.getRowNumber());

                dataEntities.ifPresent(de -> de.forEach(e -> {
                    dataList.add(e);
                    repository.save(e);
                }));
            }


            log.info("Finished processing file. Successfully parsed and saved {} list.", dataList.size());

        } catch (IOException e) {
            log.error("Error reading CSV file at path {}: {}", filePath, e.getMessage());
        }

        return dataList;
    }

    abstract protected Optional<Set<C>> parseLine(String line, int lineNumber);

    /**
     * Reads the CSV file character-by-character using a state machine to correctly
     * identify logical record boundaries, even when newlines occur inside quoted fields.
     *
     * @param br The BufferedReader of the CSV file.
     * @return A list of CsvRecord objects containing the raw line and its logical record number.
     * @throws IOException if a file reading error occurs.
     */
    protected List<CsvRow> readCsvRows(BufferedReader br) throws IOException {
        List<CsvRow> records = new ArrayList<>();

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
     * Cleans a field by trimming whitespace and removing surrounding double quotes.
     */
    protected String cleanField(String field) {
        String cleaned = field.trim();
        // Remove surrounding quotes if present
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"") && cleaned.length() > 1) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned.trim();
    }

    @RequiredArgsConstructor
    protected class CsvRow {
        @Getter
        private final String row;
        @Getter
        private final int rowNumber;
    }

}
