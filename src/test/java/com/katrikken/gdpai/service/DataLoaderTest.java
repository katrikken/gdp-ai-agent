package com.katrikken.gdpai.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style test for DataLoader to verify file system and URL loading logic.
 * Uses temporary directories and files to simulate real operation.
 */
public class DataLoaderTest {

    // Base directory for all test artifacts: temp/test-data-loader
    private static final Path TEST_BASE_DIR = Path.of("temp", "test-data-loader");
    // --- Expected Data Configuration ---
    private static final List<TestData> EXPECTED_FILES = List.of(
            new TestData("countries.txt", "Country Code, Name", "USA, Canada, Mexico\n"),
            new TestData("stats/population.csv", "Year, Population", "2024, 8.1B\n"),
            new TestData("notes/README.md", "# Data Sources", "This data is fictional and for testing purposes.\n")
    );
    private static final int EXPECTED_FILE_COUNT = 3;
    private DataLoader dataLoader;
    private Path tempUnzipDir; // Points to TEST_BASE_DIR/resources/data
    private Path tempZipFile;
    private Path temp;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Initialize DataLoader instance
        dataLoader = new DataLoader();

        tempUnzipDir = TEST_BASE_DIR.resolve("resources/data");
        Files.createDirectories(tempUnzipDir);

        // Set the @Value field 'unzipDirectoryPath' using reflection
        // This simulates the actual resources/data path from your application.
        ReflectionTestUtils.setField(dataLoader, "unzipDirectoryPath", tempUnzipDir.toString());
        temp = TEST_BASE_DIR.resolve("temp");
        ReflectionTestUtils.setField(dataLoader, "tempDirectoryPath", temp.toString());

        // Manually call the @PostConstruct method (init)
        ReflectionTestUtils.invokeMethod(dataLoader, "init");

        // Create the temporary ZIP file that will be "downloaded"
        tempZipFile = createTestZipFile(TEST_BASE_DIR.resolve("test-data-source.zip"));
    }

    /**
     * Creates a ZIP file containing the three configured files with known content
     * at the specified path.
     */
    private Path createTestZipFile(Path zipPath) throws IOException {
        // Ensure the parent directory (TEST_BASE_DIR) exists before writing the zip
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (TestData data : EXPECTED_FILES) {
                ZipEntry entry = new ZipEntry(data.name());
                zos.putNextEntry(entry);
                // Write content: First line, then a newline, then additional content
                String fullContent = data.firstLine() + "\n" + data.additionalContent();
                zos.write(fullContent.getBytes());
                zos.closeEntry();
            }
        }
        return zipPath;
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up the temporary unzip directory and its contents recursively
        if (Files.exists(tempUnzipDir)) {
            try (Stream<Path> walk = Files.walk(tempUnzipDir)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                // Suppress exceptions during cleanup
                                System.err.println("Failed to delete during cleanup: " + path + ". " + e.getMessage());
                            }
                        });
            }
        }
        // Ensure the temporary ZIP file is deleted if the DataLoader failed to do so
        if (tempZipFile != null) {
            Files.deleteIfExists(tempZipFile);
        }
    }

    @Test
    void testLoadZipFromUrlAndUnzip_ValidatesExtractedFiles() throws IOException, URISyntaxException {
        // Arrange: Use the 'file://' URL of the generated temporary ZIP file
        String fileUrl = tempZipFile.toUri().toURL().toString();

        // Act
        List<String> extractedPaths = dataLoader.loadZipFromUrlAndUnzip(fileUrl);

        // Assert 1: Check if the correct number of files were extracted
        assertEquals(EXPECTED_FILE_COUNT, extractedPaths.size(),
                "Should have extracted exactly " + EXPECTED_FILE_COUNT + " files.");

        // Assert 2: Check each extracted file for existence, name, size, and content
        for (TestData expected : EXPECTED_FILES) {
            Path extractedFile = tempUnzipDir.resolve(expected.name());

            // A. Check presence and name
            assertTrue(Files.exists(extractedFile),
                    "File should be present in the extraction directory: " + expected.name());

            // B. Check file size
            long actualSize = Files.size(extractedFile);
            assertEquals(expected.expectedSize(), actualSize,
                    "File size for '" + expected.name() + "' should match expected size.");

            // C. Check first line content
            String actualFirstLine = Files.readAllLines(extractedFile).get(0);
            assertEquals(expected.firstLine(), actualFirstLine,
                    "The first line of '" + expected.name() + "' should match the expected header.");
        }
    }

    private record TestData(String name, String firstLine, String additionalContent) {
        public long expectedSize() {
            // Calculate expected size: firstLine + additionalContent, plus line break for Java write
            return (firstLine + "\n" + additionalContent).getBytes().length;
        }
    }
}