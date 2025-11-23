package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CountryCsvParserTest {

    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryCsvParser countryCsvParser;

    private final String TEST_CSV_PATH = "data/Metadata_Country_test.csv";

    @Test
    void testLoadAndParseCsv_shouldLoadFiveValidRecords() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_CSV_PATH);
        String filePath = resource.getFile().getAbsolutePath();

        // Act
        List<Country> result = countryCsvParser.loadAndSaveCsvData(filePath);

        // Assert 1: Check that the parser returned the correct number of Country objects
        assertThat(result)
                .as("The parser should successfully return 5 Country objects")
                .hasSize(5);

        // Assert 2: Verify that the countryRepository.save() method was called exactly 5 times
        // This confirms that the persistence logic was triggered for every valid row.
        verify(countryRepository, times(5)).save(ArgumentCaptor.forClass(Country.class).capture());

        // Assert 3: Check data integrity on one of the parsed objects
        Country firstCountry = result.get(1);
        assertThat(firstCountry.getCountryCode()).isEqualTo("AFG");
        assertThat(firstCountry.getName()).isEqualTo("Afghanistan");
        assertThat(firstCountry.getIncomeGroup()).isEqualTo("Low income");
    }

}
