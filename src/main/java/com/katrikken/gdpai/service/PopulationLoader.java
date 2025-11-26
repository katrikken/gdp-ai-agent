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
public class PopulationLoader {

    @Autowired
    public final DataLoader dataLoader;
    @Autowired
    public final CountryCsvParser countryCsvParser;
    @Autowired
    public final PopulationCsvParser populationCsvParser;
    private final String POPULATION_DATA_INDICATOR = "SP.POP.TOTL";
    private final String COUNTRY_METADATA = "Metadata_Country";
    private final String METADATA = "Metadata_";
    @Value("${app.data.population-url}")
    private String populationUrl;

    @PostConstruct
    @Transactional
    public void loadGdpData() {
        try {
            List<String> files = dataLoader.loadZipFromUrlAndUnzip(populationUrl);
            if (files == null || files.isEmpty()) {
                log.error("GDP Data returned is empty");
            } else {
                Optional<String> countryDataFilePath = files.stream()
                        .filter(s -> s.startsWith(COUNTRY_METADATA))
                        .findFirst();
                countryDataFilePath.ifPresent(countryCsvParser::loadAndSaveCsvData);

                Optional<String> populationDataFilePath = files.stream()
                        .filter(s -> s.contains(POPULATION_DATA_INDICATOR) && !s.contains(METADATA))
                        .findFirst();
                populationDataFilePath.ifPresent(populationCsvParser::loadAndSaveCsvData);
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Could not load GDP zip from provided url {}", populationUrl, e);
        }
    }
}
