package com.katrikken.gdpai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    @Value("${app.data.gdp-url}")
    private String gdpUrl;

    public void loadGdpData() {
        try {
            List<String> files = dataLoader.loadZipFromUrlAndUnzip(gdpUrl);
            if (files == null || files.isEmpty()) {
                log.error("GDP Data returned is empty");
            } else {
                Optional<String> countryDataFilePath = files.stream()
                        .filter(s -> s.startsWith("Metadata_Country"))
                        .findFirst();
                countryDataFilePath.ifPresent(countryCsvParser::loadAndSaveCsvData);
            }
        } catch (IOException e) {
            log.error("Could not load GDP zip from provided url {}", gdpUrl, e);
        }
    }
}
