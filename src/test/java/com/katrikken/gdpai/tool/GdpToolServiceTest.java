package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.CountryRepository;
import com.katrikken.gdpai.repository.GdpRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(GdpToolService.class)
public class GdpToolServiceTest {
    private static final List<String> COUNTRY_CODES = Arrays.asList("USA", "CAN", "MEX", "GBR", "FRA");
    private static final List<Integer> YEARS = Arrays.asList(2020, 2021, 2022, 2023, 2024);
    private static final String TEST_COUNTRY_CODE = "USA";
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GdpRepository gdpRepository;

    @Autowired
    private GdpToolService service;

    @BeforeEach
    void setupData() {
        Country country = new Country(
                "USA",
                "USA" + " Test Name",
                "Region A",
                "High income",
                "Test notes for USA"
        );
        countryRepository.save(country);
    }


    @Test
    void gdpTrendForCountryTool_buildsExpectedTrendString() {
        // cleanup just in case
        gdpRepository.deleteAll();

        // prepare data: 2018 -> 1000, 2019 -> 1100, 2020 -> 900
        Gdp g2018 = new Gdp();
        g2018.setId(new CountryYearId("USA", 2018));
        g2018.setGdp(new BigDecimal("1000"));
        gdpRepository.save(g2018);

        Gdp g2019 = new Gdp();
        g2019.setId(new CountryYearId("USA", 2019));
        g2019.setGdp(new BigDecimal("1100"));
        gdpRepository.save(g2019);

        Gdp g2020 = new Gdp();
        g2020.setId(new CountryYearId("USA", 2020));
        g2020.setGdp(new BigDecimal("900"));
        gdpRepository.save(g2020);

        // call service - CountryCodeQuery is expected to have a constructor taking the code
        DataTool.CountryCodeQuery query = new DataTool.CountryCodeQuery("USA");
        String actual = service.gdpTrendForCountryTool(query);

        String expected =
                "GDP development for the country USA:\n" +
                        "2018: 1000\n" +
                        "2019: 1100, +100, +10.00%\n" +
                        "2020: 900, -200, -18.18%";

        assertEquals(expected, actual);
    }
}
