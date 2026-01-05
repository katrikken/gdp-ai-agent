package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.GdpPerCapita;
import com.katrikken.gdpai.repository.GdpPerCapitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class GdpPerCapitaToolService extends DataTool {


    public static final String GDP_PER_CAPITA_BY_COUNTRY_DESCRIPTION =
            "Retrieve GDP per capita records for a given country sorted by year. Input CountryCodeQuery. " +
                    "Returns a multi-line string with entries formatted as countryCode, year: gdpPerCapita.";

    public static final String GDP_PER_CAPITA_BY_YEAR_DESCRIPTION =
            "Retrieve GDP per capita records for a specific year sorted by country code. Input YearQuery. " +
                    "Returns a multi-line string with entries formatted as countryCode, year: gdpPerCapita.";

    public static final String GDP_PER_CAPITA_BY_COUNTRY_AND_RANGE_DESCRIPTION =
            "Retrieve GDP per capita records for a given country within a year range (inclusive). Input CountryCodeYearRangeQuery. " +
                    "Returns a multi-line string with entries formatted as countryCode, year: gdpPerCapita.";

    public static final String GDP_PER_CAPITA_BY_YEAR_RANGE_DESCRIPTION =
            "Retrieve GDP per capita records for all countries within a year range (inclusive). Input YearRangeQuery. " +
                    "Returns a multi-line string with entries formatted as countryCode, year: gdpPerCapita.";

    public static final String GDP_PER_CAPITA_TREND_DESCRIPTION =
            "Return a readable year-by-year GDP per capita trend for a country. Input CountryCodeQuery. " +
                    "Outputs a multi-line string starting with: \"GDP per capita development for the country {country}:\" " +
                    "followed by lines: \"{year}: {value}\" for the first record and for subsequent years: " +
                    "\"{year}: {value}, {+/-difference}, {+/-percentage%}\"";

    private final GdpPerCapitaRepository repository;


    private String formatGdpPerCapita(GdpPerCapita g) {
        if (g == null || g.getId() == null) {
            return "null record";
        }
        CountryYearId id = g.getId();
        String value = Optional.ofNullable(g.getGdpPerCapita()).map(BigDecimal::toString).orElse("null");
        return String.format("%s, %d: %s", id.getCountryCode(), id.getDataYear(), value);
    }

    @Tool(description = GDP_PER_CAPITA_BY_COUNTRY_DESCRIPTION)
    public String gdpPerCapitaByCountry(CountryCodeQuery query) {
        log.info("gdpPerCapitaByCountry called with query {}", query);
        try {
            List<GdpPerCapita> results = repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
            return results.stream().map(this::formatGdpPerCapita).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP per capita data not found for country code %s.", query.countryCode());
        }
    }

    @Tool(description = GDP_PER_CAPITA_BY_YEAR_DESCRIPTION)
    public String gdpPerCapitaByYear(YearQuery query) {
        log.info("gdpPerCapitaByYear called with query {}", query);
        try {
            List<GdpPerCapita> results = repository.findByIdDataYearOrderByIdCountryCode(query.year());
            return results.stream().map(this::formatGdpPerCapita).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP per capita data not found for year %d.", query.year());
        }
    }


    @Tool(description = GDP_PER_CAPITA_BY_COUNTRY_AND_RANGE_DESCRIPTION)
    public String gdpPerCapitaByCountryAndYearRange(CountryCodeYearRangeQuery query) {
        log.info("gdpPerCapitaByCountryAndYearRange called with query {}", query);
        try {
            List<GdpPerCapita> results = repository.findByIdCountryCodeAndIdDataYearBetweenOrderByIdDataYear(
                    query.countryCode(), query.startYear(), query.endYear());
            return results.stream().map(this::formatGdpPerCapita).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP per capita data not found for country %s between years %d and %d.",
                    query.countryCode(), query.startYear(), query.endYear());
        }
    }

    @Tool(description = GDP_PER_CAPITA_BY_YEAR_RANGE_DESCRIPTION)
    public String gdpPerCapitaByYearRange(YearRangeQuery query) {
        log.info("gdpPerCapitaByYearRange called with query {}", query);
        try {
            List<GdpPerCapita> results = repository.findByIdDataYearBetweenOrderByIdCountryCode(query.startYear(), query.endYear());
            return results.stream().map(this::formatGdpPerCapita).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP per capita data not found between years %d and %d.", query.startYear(), query.endYear());
        }
    }

    @Tool(description = GDP_PER_CAPITA_TREND_DESCRIPTION)
    public String gdpPerCapitaTrendForCountryTool(CountryCodeQuery countryCode) {
        log.info("gdpPerCapitaTrendForCountryTool called with CountryCodeQuery {}", countryCode);
        List<GdpPerCapita> results = repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
        String result = buildTrendForCountry(
                "GDP per capita development for the country",
                results,
                GdpPerCapita::getId,
                GdpPerCapita::getGdpPerCapita,
                countryCode.countryCode()
        );
        log.debug("Generated GDP per capita trend string:\n{}", result);
        return result;
    }
}
