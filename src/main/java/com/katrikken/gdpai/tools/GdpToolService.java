package com.katrikken.gdpai.tools;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import com.katrikken.gdpai.repository.GdpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
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

    // --- Input Records for Function Parameters ---

    /**
     * AI Tool: Retrieves all the Gross Domestic Product (GDP) values.
     *
     * @return a Function
     */
    @Bean
    @Description("Returns all known GDP data (List).")
    public Function<Void, List<Gdp>> getAllGdpTool() {
        return (Void) -> repository.findAll();
    }

    /**
     * AI Tool: Inserts Gross Domestic Product (GDP) values.
     *
     * @return a Function
     */
    @Bean
    @Description("Inserts GDP data (Gdp).")
    public Function<Gdp, Gdp> insertGdpTool() {
        return repository::save;
    }

    /**
     * AI Tool: Retrieves the Gross Domestic Product (GDP) value for a specific country and year.
     *
     * @return a Function
     */
    @Description("Takes a CountryCodeYearQuery (countryCode, year) and returns GDP data (String).")
    @Bean
    public Function<CountryCodeYearQuery, String> gdpByCountryCodeYearTool() {
        return (CountryCodeYearQuery query) -> {
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

    /**
     * AI Tool: Retrieves all data on the Gross Domestic Product (GDP) for a specific country.
     *
     * @return a Function
     */
    @Description("Takes a CountryCodeQuery (countryCode) and returns GDP data list sorted by year (List).")
    @Bean
    public Function<CountryCodeQuery, List> gdpByCountryCodeTool() {
        return (CountryCodeQuery countryCode) -> {
            try {
                return repository.findByIdCountryCodeOrderByIdDataYear(countryCode.countryCode());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of(String.format("Error: GDP data not found for country code %s.", countryCode));
            }
        };
    }

    /**
     * AI Tool: Retrieves all data on the Gross Domestic Product (GDP) for a specific year.
     *
     * @return a Function
     */
    @Description("Takes a YearQuery (year) and returns GDP data list sorted by Country Code (List).")
    @Bean
    public Function<YearQuery, List> gdpByYearTool() {
        return (YearQuery year) -> {
            try {
                return repository.findByIdDataYearOrderByIdCountryCode(year.year());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return List.of(String.format("Error: GDP data not found for year %s.", year));
            }
        };
    }

    /**
     * AI Tool: Retrieves all data on the Gross Domestic Product (GDP) for a specific year period.
     *
     * @return a Function
     */
    @Description("Takes a YearRangeQuery (startYear, endYear) and returns GDP data list sorted by Country Code (List).")
    @Bean
    public Function<YearRangeQuery, List<Gdp>> gdpBetweenYearTool() {
        return (YearRangeQuery interval) -> repository
                .findByIdDataYearBetweenOrderByIdCountryCode(interval.startYear(), interval.endYear());
    }

    /**
     * AI Tool: Sorts data on Gross Domestic Product (GDP) by value from lowest to highest.
     *
     * @return a Function
     */
    @Description("Takes a list of Gdp class (List) and returns the list sorted by GDP value (List).")
    @Bean
    public Function<List<Gdp>, List<Gdp>> gdpSortByGdpValueTool() {
        return (List<Gdp> gdp) -> gdp.stream().sorted(Comparator.comparing(Gdp::getGdp)).toList();
    }

}