package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.GdpPerCapita;
import com.katrikken.gdpai.repository.GdpPerCapitaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class GdpPerCapitaToolService extends DataTool {

    private final GdpPerCapitaRepository repository;

    @Tool(description = "A Java Function that takes a CountryCodeQuery record and returns a list of GdpPerCapita entities for a specific country.")
    public List<GdpPerCapita> gdpPerCapitaByCountry(CountryCodeQuery query) {
        log.info("gdpPerCapitaByCountry called with query {}", query);
        return repository.findByIdCountryCodeOrderByIdDataYear(query.countryCode());
    }

    @Tool(description = "Takes a YearQuery record and returns a list of GdpPerCapita entities for a specific year.")
    public List<GdpPerCapita> gdpPerCapitaByYear(YearQuery query) {
        log.info("gdpPerCapitaByYear called with query {}", query);
        return repository.findByIdDataYearOrderByIdCountryCode(query.year());
    }

    @Tool(description = "Takes a YearRangeQuery record and returns the GDP per capita values for all countries within a given start year and end year range (inclusive).")
    public List<GdpPerCapita> gdpPerCapitaByYearRange(YearRangeQuery query) {
        log.info("gdpPerCapitaByYearRange called with query {}", query);
        return repository.findByIdDataYearBetweenOrderByIdCountryCode(query.startYear(), query.endYear());
    }
}
