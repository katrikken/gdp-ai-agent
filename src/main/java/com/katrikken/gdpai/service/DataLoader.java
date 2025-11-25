package com.katrikken.gdpai.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@Log4j2
public class DataLoader {

    // Buffer size for reading streams
    private static final int BUFFER_SIZE = 4096;
    // Set to store the URLs that have already been successfully downloaded and processed.
    private final Set<String> loadedUrls = Collections.synchronizedSet(new HashSet<>());
    @Value("${app.data.unzip-dir:data}")
    private String unzipDirectoryPath;
    @Value("${app.data.temp-dir:temp}")
    private String tempDirectoryPath;
    // The actual Path object for the target extraction directory
    private Path targetDir;
    private Path tempDir;

    /**
     * Initializes the target directory structure on startup.
     */
    @PostConstruct
    private void init() throws IOException {
        this.targetDir = Paths.get(unzipDirectoryPath);
        // Create the directory if it does not exist
        Files.createDirectories(this.targetDir);

        this.tempDir = Paths.get(tempDirectoryPath);
        // Create the directory if it does not exist
        Files.createDirectories(this.tempDir);

        log.info("Initialized DataLoader. Unzipped files will be stored in: {}", this.targetDir.toAbsolutePath());
    }

    /**
     * Downloads a ZIP file from the given URL, unzips its contents, and caches the URL.
     *
     * @param url The public URL of the ZIP file to download.
     * @return A list of absolute file paths to the newly extracted files.
     * @throws IOException if there is an issue with downloading or file handling.
     */
    public List<String> loadZipFromUrlAndUnzip(String url) throws IOException, URISyntaxException {
        if (loadedUrls.contains(url)) {
            log.warn("URL '{}' has already been loaded. Skipping download.", url);
            return List.of();
        }

        log.info("Starting download for ZIP file from URL: {}", url);
        Path tempZipFile = null;
        List<String> extractedFiles;

        try {
            URL zipUrl = new URL(url);
            //todo fix
            Path downloadedZipPath = Paths.get(zipUrl.toURI());
            String fileName = downloadedZipPath.getFileName().toString();
            tempZipFile = tempDir.resolve(fileName + "-.zip");

            try (InputStream in = zipUrl.openStream();
                 FileOutputStream fos = new FileOutputStream(tempZipFile.toFile())) {

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            log.info("Downloaded ZIP file successfully to: {}", tempZipFile.toAbsolutePath());

            extractedFiles = unzip(tempZipFile, targetDir);

            loadedUrls.add(url);
            log.info("Successfully extracted {} files and cached the URL.", extractedFiles.size());

        } catch (IOException e) {
            log.error("Failed to download or unzip data from URL {}: {}", url, e.getMessage(), e);
            throw e;
        } catch (URISyntaxException ex) {
            log.error("Failed to get URL {}: {}", url, ex.getMessage(), ex);
            throw ex;
        } finally {
            // Clean up the temporary downloaded ZIP file
            if (tempZipFile != null) {
                try {
                    Files.deleteIfExists(tempZipFile);
                    log.debug("Deleted temporary ZIP file: {}", tempZipFile.toAbsolutePath());
                } catch (IOException e) {
                    // Log but do not interrupt the flow
                    log.warn("Could not delete temporary file: {}", tempZipFile, e);
                }
            }
        }

        return extractedFiles;
    }

    /**
     * Unzips the content of a ZIP file to a specified target directory.
     * This method handles creating necessary directories and preventing ZipSlip.
     *
     * @param zipFile   The path to the downloaded ZIP file.
     * @param targetDir The directory where files will be extracted.
     * @return A list of absolute paths for the extracted files.
     * @throws IOException if extraction fails.
     */
    private List<String> unzip(Path zipFile, Path targetDir) throws IOException {
        List<String> extractedFiles = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                // Ensure entry is not pointing outside the target directory (ZipSlip prevention)
                Path newFile = targetDir.resolve(zipEntry.getName()).normalize();

                // Security check: ensure the file path is a sub-path of the target directory
                if (!newFile.startsWith(targetDir)) {
                    throw new IOException("Zip entry attempted to create file outside of target directory: " + zipEntry.getName());
                }

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newFile);
                } else {
                    // Create parent directories if they don't exist
                    Path parent = newFile.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    // Write the file content
                    try (FileOutputStream fos = new FileOutputStream(newFile.toFile())) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    extractedFiles.add(newFile.toAbsolutePath().toString());
                    log.debug("Extracted file: {}", newFile.toAbsolutePath());
                }

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        }
        return extractedFiles;
    }
}
