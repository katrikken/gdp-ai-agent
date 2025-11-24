package com.katrikken.gdpai.service;

import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.GdpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GdpCsvParserTest {

    private final String TEST_CSV_PATH = "data/Gdp_data_test.csv";

    @Mock
    private GdpRepository gdpRepository;
    @InjectMocks
    private GdpCsvParser parser;

    @Test
    void testLoadAndParseCsv_shouldLoadValidRecords() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_CSV_PATH);
        String filePath = resource.getFile().getAbsolutePath();

        // Act
        List<Gdp> result = parser.loadAndSaveCsvData(filePath);

        // Assert 1: Check that the parser returned the correct number of Country objects
        assertThat(result)
                .as("The parser should successfully return objects")
                .hasSize(237);

        // Assert 2: Verify that the countryRepository.save() method was called exactly 5 times
        // This confirms that the persistence logic was triggered for every valid row.
        verify(gdpRepository, times(237)).save(ArgumentCaptor.forClass(Gdp.class).capture());

        // Assert 3: Check data integrity on one of the parsed objects
        List<Gdp> gdp = result.stream().filter(g -> g.getId().getCountryCode().equals("ABW"))
                .sorted(Comparator.comparingInt(d -> d.getId().getDataYear()))
                .toList();
        assertThat(gdp).isNotEmpty().hasSize(38);
        Gdp firstGdp = gdp.get(0);
        assertThat(firstGdp.getId().getCountryCode()).isEqualTo("ABW");
        assertThat(firstGdp.getId().getDataYear()).isEqualTo(1986);
        assertThat(firstGdp.getGdp()).isEqualTo(new BigDecimal("405586592.178771"));

        gdp = result.stream().filter(g -> g.getId().getCountryCode().equals("AGO"))
                .sorted(Comparator.comparingInt(d -> d.getId().getDataYear()))
                .toList();
        assertThat(gdp).isNotEmpty().hasSize(45);
        firstGdp = gdp.get(0);
        assertThat(firstGdp.getId().getCountryCode()).isEqualTo("AGO");
        assertThat(firstGdp.getId().getDataYear()).isEqualTo(1980);
        assertThat(firstGdp.getGdp()).isEqualTo(new BigDecimal("5930503400.96263"));
        Gdp lastGdp = gdp.get(44);
        assertThat(lastGdp.getGdp()).isEqualTo(new BigDecimal("80396942241.6233"));
    }

}
