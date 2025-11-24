package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.Population;
import com.katrikken.gdpai.repository.PopulationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PopulationCsvParserTest {

    private final String TEST_CSV_PATH = "data/Population_data_test.csv";

    @Mock
    private PopulationRepository repository;
    @InjectMocks
    private PopulationCsvParser parser;

    @Test
    void testLoadAndParseCsv_shouldLoadValidRecords() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_CSV_PATH);
        String filePath = resource.getFile().getAbsolutePath();

        // Act
        List<Population> result = parser.loadAndSaveCsvData(filePath);

        // Assert 1: Check that the parser returned the correct number of Country objects
        assertThat(result)
                .as("The parser should successfully return objects")
                .hasSize(260);

        // Assert 2: Verify that the countryRepository.save() method was called exactly 5 times
        // This confirms that the persistence logic was triggered for every valid row.
        verify(repository, times(260)).save(ArgumentCaptor.forClass(Population.class).capture());

        // Assert 3: Check data integrity on one of the parsed objects
        List<Population> populations = result.stream().filter(g -> g.getId().getCountryCode().equals("AFE"))
                .sorted(Comparator.comparingInt(d -> d.getId().getDataYear()))
                .toList();
        assertThat(populations).isNotEmpty().hasSize(65);
        Population first = populations.get(0);
        assertThat(first.getId().getCountryCode()).isEqualTo("AFE");
        assertThat(first.getId().getDataYear()).isEqualTo(1960);
        assertThat(first.getPopulation()).isEqualTo(130075728L);

        populations = result.stream().filter(g -> g.getId().getCountryCode().equals("AGO"))
                .sorted(Comparator.comparingInt(d -> d.getId().getDataYear()))
                .toList();
        assertThat(populations).isNotEmpty().hasSize(65);
        first = populations.get(0);
        assertThat(first.getId().getCountryCode()).isEqualTo("AGO");
        assertThat(first.getId().getDataYear()).isEqualTo(1960);
        assertThat(first.getPopulation()).isEqualTo(5231654L);
        Population last = populations.get(64);
        assertThat(last.getPopulation()).isEqualTo(37885849L);

        assertThat(
                result.stream().filter(g -> g.getId().getCountryCode().equals("INX")))
                .isEmpty();
    }

}
