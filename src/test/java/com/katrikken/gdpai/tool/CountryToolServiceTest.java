package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(CountryToolService.class)
public class CountryToolServiceTest {

    // Mock the dependency
    @Autowired
    private CountryRepository repository;
    @Autowired
    private CountryToolService countryToolService;


    @BeforeEach
    void setUp() {
        repository.save(Country.builder().countryCode("ABW").name("Aruba").build());
        repository.save(Country.builder().countryCode("GBR").name("United Kingdom").build());
        repository.save(Country.builder().countryCode("USA").name("United States").build());
        repository.save(Country.builder().countryCode("ARE").name("United Arab Emirates").build());
        assertThat(repository.findAll()).hasSize(4);
    }


    @Test
    void countryNameToCountryCodeTool_SingleMatch_ReturnsCode() {

        // THEN: Call the tool and assert the correct country code is returned
        String result = countryToolService.countryNameToCountryCodeTool("Aruba");
        assertEquals("ABW", result,
                "Should return the correct country code for a single match.");
    }

    @Test
    void countryNameToCountryCodeTool_PartialMatch_ReturnsCode() {

        String result = countryToolService.countryNameToCountryCodeTool("Kingdom");
        assertEquals("GBR", result,
                "Should return the correct country code even with partial input if only one match is found.");
    }

    @Test
    void countryNameToCountryCodeTool_NoMatch_ReturnsError() {
        String result = countryToolService.countryNameToCountryCodeTool("Country");
        assertEquals("Error: could not find Country code for provided Country name Country", result,
                "Should return a 'not found' error message.");
    }

    @Test
    void countryNameToCountryCodeTool_MultipleMatches_ReturnsError() {
        String result = countryToolService.countryNameToCountryCodeTool("United");
        assertEquals("Error: several countries match provided country name United", result,
                "Should return an 'ambiguous match' error message when multiple results are found.");
    }


}