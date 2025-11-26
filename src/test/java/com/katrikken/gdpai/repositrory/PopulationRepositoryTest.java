package com.katrikken.gdpai.repositrory;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.CountryRepository;
import com.katrikken.gdpai.repository.PopulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PopulationRepositoryTest {

    private static final List<String> COUNTRY_CODES = Arrays.asList("USA", "CAN", "MEX", "GBR", "FRA");
    private static final List<Integer> YEARS = Arrays.asList(2020, 2021, 2022, 2023, 2024);
    private static final String TEST_COUNTRY_CODE = "USA";
    private static final Integer TEST_YEAR = 2022;
    @Autowired
    private PopulationRepository populationRepository;
    @Autowired
    private CountryRepository countryRepository;

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
     * Data setup method run before each test.
     * Inserts 5 countries * 5 years = 25 records into Population table.
     */
    @BeforeEach
    void setupData() {
        setupCountries(COUNTRY_CODES);

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

    @Test
    void testFindPopulationByIdCountryCode_shouldReturnAllFiveYearsOfData() {
        List<Population> result = populationRepository.findByIdCountryCodeOrderByIdDataYear(TEST_COUNTRY_CODE);

        assertThat(result)
                .as("Check if 5 records are returned for the test country")
                .hasSize(5);

        assertThat(result.get(0).getId().getDataYear()).isEqualTo(2020);

        assertThat(result.stream().map(d -> d.getId().getCountryCode()).allMatch(TEST_COUNTRY_CODE::equals))
                .as("Check that all returned records belong to the correct country")
                .isTrue();
    }

    @Test
    void testFindPopulationByIdYear_shouldReturnAllFiveCountriesData() {

        List<Population> result = populationRepository.findByIdDataYearOrderByIdCountryCode(TEST_YEAR);


        assertThat(result)
                .as("Check if 5 records are returned for the test year")
                .hasSize(5);

        assertThat(result.getFirst().getId().getCountryCode()).isEqualTo("CAN");

        assertThat(result.stream().map(d -> d.getId().getDataYear()).allMatch(TEST_YEAR::equals))
                .as("Check that all returned records belong to the correct year")
                .isTrue();
    }

    @Test
    void testFindPopulationByIdCountryCodeAndIdYear_shouldReturnSingleRecord() {

        Population result = populationRepository.findByIdCountryCodeAndIdDataYear(TEST_COUNTRY_CODE, TEST_YEAR);


        assertThat(result)
                .as("Check that a single, non-null record is returned")
                .isNotNull();

        assertThat(result.getId().getCountryCode())
                .as("Check the country code of the returned record")
                .isEqualTo(TEST_COUNTRY_CODE);

        assertThat(result.getId().getDataYear())
                .as("Check the year of the returned record")
                .isEqualTo(TEST_YEAR);
    }
}
