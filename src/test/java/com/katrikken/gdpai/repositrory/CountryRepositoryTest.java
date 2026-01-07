package com.katrikken.gdpai.repositrory;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CountryRepositoryTest {

    private static final List<String> COUNTRY_CODES = Arrays.asList("USA", "CAN", "MEX", "GBR", "FRA");
    private static final String TEST_COUNTRY_CODE = "USA";

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    void setupData() {
        countryRepository.deleteAll();
        setupCountries(COUNTRY_CODES);
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

    @Test
    void testFindByNameContaining_shouldReturnMatchingCountries() {
        List<Country> result = countryRepository.findByNameContaining("Test Name");

        assertThat(result)
                .as("Check that all countries with 'Test Name' in their name are returned")
                .hasSize(5);

        assertThat(result.stream().allMatch(c -> c.getName().contains("Test Name")))
                .isTrue();
    }

    @Test
    void testFindById_shouldReturnCountry() {
        Optional<Country> opt = countryRepository.findById(TEST_COUNTRY_CODE);

        assertThat(opt).isPresent();
        Country country = opt.get();
        assertThat(country.getCountryCode()).isEqualTo(TEST_COUNTRY_CODE);
        assertThat(country.getName()).isEqualTo(TEST_COUNTRY_CODE + " Test Name");
    }

    @Test
    void testFindAll_shouldReturnAllCountries() {
        List<Country> all = countryRepository.findAll();
        assertThat(all).hasSize(5);
    }
}
