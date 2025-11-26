package com.katrikken.gdpai.tools;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class CountryToolService {

    private final CountryRepository repository;

    /**
     * AI Tool: Converts Country name to Country code for querying data in the database
     *
     * @return a Function
     */
    @Description("Takes a country name (String) and returns corresponding country code (String)")
    @Bean
    public Function<String, String> countryNameToCountryCodeTool() {
        return (String name) -> {
            List<Country> countries = repository.findByNameContaining(name);
            if (countries.isEmpty()) {
                return String.format("Error: could not find Country code for provided Country name %s", name);
            } else if (countries.size() != 1) {
                return String.format("Error: several countries match provided country name %s", name);
            }

            return countries.getFirst().getCountryCode();
        };
    }
}