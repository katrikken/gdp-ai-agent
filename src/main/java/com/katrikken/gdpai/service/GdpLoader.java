package com.katrikken.gdpai.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class GdpLoader {

    @Autowired
    public final DataLoader dataLoader;
    @Autowired
    public final CountryCsvParser countryCsvParser;
    @Autowired
    public final GdpCsvParser gdpCsvParser;
    private final String GDP_DATA_INDICATOR = "NY.GDP.MKTP.CD";
    private final String COUNTRY_METADATA = "Metadata_Country";
    private final String METADATA = "Metadata_";
    @Value("${app.data.gdp-url}")
    private String gdpUrl;

    @PostConstruct
    @Transactional
    public void loadGdpData() {
        try {
            List<String> files = dataLoader.loadZipFromUrlAndUnzip(gdpUrl);
            if (files == null || files.isEmpty()) {
                log.error("GDP Data returned is empty");
            } else {
                Optional<String> countryDataFilePath = files.stream()
                        .filter(s -> s.contains(COUNTRY_METADATA))
                        .findFirst();
                countryDataFilePath.ifPresent(countryCsvParser::loadAndSaveCsvData);

                Optional<String> gdpDataFilePath = files.stream()
                        .filter(s -> s.contains(GDP_DATA_INDICATOR) && !s.contains(METADATA))
                        .findFirst();
                gdpDataFilePath.ifPresent(gdpCsvParser::loadAndSaveCsvData);
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Could not load GDP zip from provided url {}", gdpUrl, e);
        }
    }
}
