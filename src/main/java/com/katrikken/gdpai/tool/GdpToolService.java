package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.GdpRepository;
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
public class GdpToolService extends DataTool {

    public static final String INSERT_GDP_DESCRIPTION =
            "Insert a GDP record. Input a Gdp entity. Returns a single formatted string: countryCode, year: gdpValue.";
    public static final String GET_GDP_BY_COUNTRY_YEAR_DESCRIPTION =
            "Retrieve a single GDP record for a specific country code and year. Input CountryCodeYearQuery. " +
                    "Returns a single formatted string: countryCode, year: gdpValue or an explanatory message if not found.";
    public static final String GET_GDP_BY_COUNTRY_DESCRIPTION =
            "Retrieve all GDP records for a given country, found by country code sorted by year. Input CountryCodeQuery. " +
                    "Returns a list of formatted strings: countryCode, year: gdpValue.";
    public static final String GET_GDP_BY_YEAR_DESCRIPTION =
            "Retrieve all GDP records for a specific year sorted by country code. Input YearQuery. " +
                    "Returns a list of formatted strings: countryCode, year: gdpValue.";
    public static final String GET_GDP_BETWEEN_YEARS_DESCRIPTION =
            "Retrieve GDP records between two years inclusive sorted by country code. Input YearRangeQuery. " +
                    "Returns a list of formatted strings: countryCode, year: gdpValue.";
    public static final String GDP_TREND_DESCRIPTION =
            "Return GDP historical trend for a country. Input CountryCodeQuery. " +
                    "Outputs a multi-line string starting with GDP development for the country including growth percentage.";


    private final GdpRepository repository;

    private String formatGdp(Gdp g) {
        if (g == null) {
            return "null record";
        }
        CountryYearId id = g.getId();
        String value = Optional.ofNullable(g.getGdp()).map(BigDecimal::toString).orElse("null");
        return String.format("%s, %d: %s", id.getCountryCode(), id.getDataYear(), value);
    }

    @Tool(description = INSERT_GDP_DESCRIPTION)
    public String insertGdpTool(Gdp gdp) {
        log.info("insertGdpTool called with GDP {}", gdp);
        Gdp saved = repository.save(gdp);
        return formatGdp(saved);
    }


    @Tool(description = GET_GDP_BY_COUNTRY_YEAR_DESCRIPTION)
    public String gdpByCountryCodeYearTool(CountryCodeYearQuery query) {
        log.info("gdpByCountryCodeYearTool called with CountryCodeYearQuery {}", query);
        try {
            Optional<Gdp> gdp = repository.findById(new CountryYearId(query.countryCode(), query.year()));
            if (gdp.isPresent()) {
                return formatGdp(gdp.get());
            } else {
                return String.format("GDP data are not available for country code %s in year %d",
                        query.countryCode(), query.year());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP data not found for country code %s in year %d.",
                    query.countryCode(), query.year());
        }
    }

    @Tool(description = GET_GDP_BY_COUNTRY_DESCRIPTION)
    public String gdpByCountryCodeTool(CountryCodeQuery countryCode) {
        log.info("gdpByCountryCodeTool called with CountryCodeQuery {}", countryCode);
        try {
            List<Gdp> results = repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
            return results.stream().map(this::formatGdp).collect(Collectors.joining("\n"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return String.format("Error: GDP data not found for country code %s.", countryCode.countryCode());
        }
    }

    @Tool(description = GET_GDP_BY_YEAR_DESCRIPTION)
    public String gdpByYearTool(YearQuery year) {
        log.info("gdpByYearTool called with YearQuery {}", year);
        List<Gdp> results = repository.findByIdDataYearOrderByIdCountryCode(year.year());
        return results.stream().map(this::formatGdp).collect(Collectors.joining("\n"));
    }

    @Tool(description = GET_GDP_BETWEEN_YEARS_DESCRIPTION)
    public String gdpBetweenYearTool(YearRangeQuery interval) {
        log.info("gdpBetweenYearTool called with YearRangeQuery {}", interval);
        List<Gdp> results = repository.findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
        return results.stream().map(this::formatGdp).collect(Collectors.joining("\n"));
    }

    @Tool(description = GDP_TREND_DESCRIPTION)
    public String gdpTrendForCountryTool(CountryCodeQuery countryCode) {
        log.info("gdpTrendForCountryTool called with CountryCodeQuery {}", countryCode);
        List<Gdp> results = repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
        String result = buildTrendForCountry(
                "GDP development for the country",
                results,
                Gdp::getId,
                Gdp::getGdp,
                countryCode.countryCode()
        );
        log.debug("Generated GDP trend string:\n{}", result);
        return result;
    }

}