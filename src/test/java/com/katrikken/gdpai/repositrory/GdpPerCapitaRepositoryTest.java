package com.katrikken.gdpai.repositrory;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.entity.GdpPerCapita;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.CountryRepository;
import com.katrikken.gdpai.repository.GdpPerCapitaRepository;
import com.katrikken.gdpai.repository.GdpRepository;
import com.katrikken.gdpai.repository.PopulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
public class GdpPerCapitaRepositoryTest {

    private static final List<String> COUNTRY_CODES = Arrays.asList("USA", "CAN", "MEX", "GBR", "FRA");
    private static final List<Integer> YEARS = Arrays.asList(2020, 2021, 2022, 2023, 2024);
    @Autowired
    private GdpPerCapitaRepository gdpPerCapitaRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GdpRepository gdpRepository;
    @Autowired
    private PopulationRepository populationRepository;

    /**
     * Data setup method run before each test.
     * Inserts 5 countries * 5 years = 25 records into both GDP table.
     */
    @BeforeEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void setupData() {
        setupCountries(COUNTRY_CODES);
        BigDecimal gdpCounter = new BigDecimal(10000000000L); // Start with a large base number for GDP

        for (String countryCode : COUNTRY_CODES) {
            for (int year : YEARS) {
                CountryYearId id = new CountryYearId(countryCode, year);
                Gdp Gdp = new Gdp(id, gdpCounter);
                gdpRepository.save(Gdp);
                gdpCounter.add(new BigDecimal(100000000L));
            }
        }

        long popCounter = 100000000L;   // Start with a large base number for Population

        for (String countryCode : COUNTRY_CODES) {
            for (int year : YEARS) {
                CountryYearId id = new CountryYearId(countryCode, year);

                Population Population = new Population(id, popCounter);
                populationRepository.save(Population);
                popCounter += 1000000L;
            }
        }
    }

    private void setupCountries(List<String> countryCodes) {
        for (String code : countryCodes) {
            Country country = new Country(
                    code,
                    code + " Test Name",
                    "Region A",
                    "High income",
                    "Test notes for " + code
            );
            countryRepository.save(country);
        }
    }


    /**
     * Tests the retrieval of all records for a specific country using findByIdCountryCode.
     * Expected: 5 records for USA (2020-2024).
     */
    @Test
    void testFindByIdCountryCode_Success() {
        List<GdpPerCapita> all = gdpPerCapitaRepository.findAll();
        assertThat(all).isNotEmpty().hasSize(25);

        String countryCode = "USA";
        List<GdpPerCapita> results = gdpPerCapitaRepository.findByIdCountryCodeOrderByIdDataYear(countryCode);

        // 1. Assert size: We expect 5 years of data for the USA
        assertEquals(5, results.size(), "Should return 5 records for the USA.");

        // 2. Assert calculated value precision (USA, 2024)
        // Data from data.sql (USA 2024): GDP=27,500,000,000,000, POPULATION=336,000,000
        // Expected result: 27500000000000 / 336000000 ≈ 81845.2380952381
        GdpPerCapita usa2024 = results.stream()
                .filter(gpc -> gpc.getId().getDataYear() == 2024)
                .findFirst()
                .orElseThrow(() -> new AssertionError("USA 2024 data not found."));

        assertEquals("USA", usa2024.getId().getCountryCode());
        assertEquals(2024, usa2024.getId().getDataYear());
        assertEquals(new BigDecimal("81845.2380952381"), usa2024.getGdpPerCapita());
    }

    /**
     * Tests the retrieval of all countries for a specific year using findByIdYear.
     * Expected: 5 records for 2023 (USA, CAN, MEX, GBR, FRA).
     */
    @Test
    void testFindByIdYear_Success() {
        int targetYear = 2023;
        List<GdpPerCapita> results = gdpPerCapitaRepository.findByIdDataYearOrderByIdCountryCode(targetYear);

        // Assert size: We expect 5 countries with data for 2023
        assertEquals(5, results.size(), "Should return 5 country records for the year 2023.");

        // Ensure at least one known country (e.g., CAN) is present
        assertFalse(results.stream()
                        .noneMatch(gpc -> gpc.getId().getCountryCode().equals("CAN")),
                "CAN should be included in the 2023 results.");
    }

    /**
     * Tests the retrieval of records within a specified year range using findByIdYearBetween.
     * Expected: 5 countries * 3 years (2021, 2022, 2023) = 15 records.
     */
    @Test
    void testFindByIdYearBetween_Success() {
        int startYear = 2021;
        int endYear = 2023;
        List<GdpPerCapita> results = gdpPerCapitaRepository.findByIdDataYearBetweenOrderByIdCountryCode(startYear, endYear);

        // Expected total records: (2023 - 2021 + 1) * 5 countries = 15
        assertEquals(15, results.size(), "Should return 15 records (5 countries * 3 years).");
    }

    /**
     * Tests retrieval when no data exists for the requested query.
     */
    @Test
    void testFindByIdCountryCode_NotFound() {
        String missingCode = "ZZZ"; // Not in data.sql
        List<GdpPerCapita> results = gdpPerCapitaRepository.findByIdCountryCodeOrderByIdDataYear(missingCode);
        assertEquals(0, results.size(), "Should return an empty list for non-existent country code.");
    }

    /**
     * Tests retrieval when no data exists for the requested year.
     */
    @Test
    void testFindByIdYear_NotFound() {
        int missingYear = 2050; // Not in data.sql
        List<GdpPerCapita> results = gdpPerCapitaRepository.findByIdDataYearOrderByIdCountryCode(missingYear);
        assertEquals(0, results.size(), "Should return an empty list for a year outside the dataset.");
    }
}
