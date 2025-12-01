package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.GdpPerCapita;
import com.katrikken.gdpai.repository.GdpPerCapitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class GdpPerCapitaToolService extends DataTool {

    private final GdpPerCapitaRepository repository;

    @Tool(description = "A Java Function that takes a CountryCodeQuery record and returns a map of Year to GdpPerCapita value for a specific country.")
    public Map<Integer, BigDecimal> gdpPerCapitaByCountry(CountryCodeQuery query) {
        log.info("gdpPerCapitaByCountry called with query {}", query);
        return repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode()).stream()
                .collect(Collectors.toMap(v -> v.getId().getDataYear(), GdpPerCapita::getGdpPerCapita));
    }

    @Tool(description = "Takes a YearQuery record and returns a map of Country name to GdpPerCapita value for a specific year.")
    public Map<String, BigDecimal> gdpPerCapitaByYear(YearQuery query) {
        log.info("gdpPerCapitaByYear called with query {}", query);
        return repository.findByIdDataYearOrderByIdCountryCode(query.year()).stream()
                .collect(Collectors.toMap(GdpPerCapita::getName, GdpPerCapita::getGdpPerCapita));
    }

    @Tool(description = "Takes a CountryCodeYearRangeQuery record and returns map of Year to the GDP per capita value for a given country " +
            "within a given start year and end year range (inclusive).")
    public Map<Integer, BigDecimal> gdpPerCapitaByCountryAndYearRange(CountryCodeYearRangeQuery query) {
        log.info("gdpPerCapitaByCountryAndYearRange called with query {}", query);
        return repository.findByIdDataYearBetweenOrderByIdCountryCode(query.startYear(), query.endYear()).stream()
                .collect(Collectors.toMap(v -> v.getId().getDataYear(), GdpPerCapita::getGdpPerCapita));
    }

    @Tool(description = "Takes a YearRangeQuery record and returns the GDP per capita values for all countries within a given start year and end year range (inclusive).")
    public List<GdpPerCapita> gdpPerCapitaByYearRange(YearRangeQuery query) {
        log.info("gdpPerCapitaByYearRange called with query {}", query);
        return repository.findByIdDataYearBetweenOrderByIdCountryCode(query.startYear(), query.endYear());
    }

    @Tool(description = "Takes a list of GdpPerCapita (List) and returns the map of Country codes to the list of GdpPerCapita values (List).")
    public Map<String, List<GdpPerCapita>> mapGdpPerCapitaByCountryTool(List<GdpPerCapita> GDPs) {
        log.info("mapGdpPerCapitaByCountryTool called");
        return GDPs.stream().collect(Collectors.groupingBy(
                p -> p.getId().getCountryCode(), Collectors.toList()
        ));
    }

    @Tool(description = "Takes a list of GdpPerCapita (List) and returns the map of Years to the list of GdpPerCapita values (List).")
    public Map<Integer, List<GdpPerCapita>> mapGdpPerCapitaByYearTool(List<GdpPerCapita> GDPs) {
        log.info("mapGdpPerCapitaByYearTool called");
        return GDPs.stream().collect(Collectors.groupingBy(
                p -> p.getId().getDataYear(), Collectors.toList()
        ));
    }
}
