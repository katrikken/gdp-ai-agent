package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.GdpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class GdpToolService extends DataTool {

    private final GdpRepository repository;

    @Tool(description = "Returns all known GDP data (List).")
    public Function<Void, List<Gdp>> getAllGdpTool() {
        log.info("getAllGdpTool called");
        return (Void) -> repository.findAll();
    }

    @Tool(description = "Inserts GDP data (Gdp).")
    public Function<Gdp, Gdp> insertGdpTool() {
        return (Gdp gdp) -> {
            log.info("insertGdpTool called with GDP {}", gdp);
            return repository.save(gdp);
        };

    }

    @Tool(description = "Takes a CountryCodeYearQuery (countryCode, year) and returns GDP data (String).")
    public Function<CountryCodeYearQuery, String> gdpByCountryCodeYearTool() {
        return (CountryCodeYearQuery query) -> {
            log.info("gdpByCountryCodeYearTool called with CountryCodeYearQuery {}", query);
            try {
                Optional<Gdp> gdp = repository.findById(new CountryYearId(query.countryCode(), query.year()));
                if (gdp.isPresent()) {
                    return String.valueOf(gdp.get().getGdp());
                } else {
                    return String.format("GDP data are not available for country code %s in year %d",
                            query.countryCode(), query.year());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return String.format("Error: GDP data not found for country code %s in year %d.",
                        query.countryCode(), query.year());
            }
        };
    }

    @Tool(description = "Takes a CountryCodeQuery (countryCode) and returns GDP data list sorted by year (List).")
    public Function<CountryCodeQuery, List> gdpByCountryCodeTool() {
        return (CountryCodeQuery countryCode) -> {
            log.info("gdpByCountryCodeTool called with CountryCodeQuery {}", countryCode);
            try {
                return repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of(String.format("Error: GDP data not found for country code %s.", countryCode));
            }
        };
    }

    @Tool(description = "Takes a YearQuery (year) and returns GDP data list sorted by Country Code (List).")
    public Function<YearQuery, List> gdpByYearTool() {
        return (YearQuery year) -> {
            log.info("gdpByYearTool called with YearQuery {}", year);
            try {
                return repository.findByIdDataYearOrderByIdCountryCode(year.year());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of(String.format("Error: GDP data not found for year %s.", year));
            }
        };
    }

    @Tool(description = "Takes a YearRangeQuery (startYear, endYear) and returns GDP data list sorted by Country Code (List).")
    public Function<YearRangeQuery, List<Gdp>> gdpBetweenYearTool() {
        return (YearRangeQuery interval) -> {
            log.info("gdpBetweenYearTool called with YearRangeQuery {}", interval);
            return repository
                    .findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
        };
    }

    @Tool(description = "Takes a list of Gdp class (List) and returns the list sorted by GDP value (List).")
    public Function<List<Gdp>, List<Gdp>> gdpSortByGdpValueTool() {
        log.info("gdpSortByGdpValueTool called");
        return (List<Gdp> gdp) -> gdp.stream().sorted(Comparator.comparing(Gdp::getGdp)).toList();
    }
}