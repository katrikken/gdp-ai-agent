package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
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
    @Tool(description = "Takes a country name (String) and returns corresponding country code (String)")
    public Function<String, String> countryNameToCountryCodeTool() {
        return (String name) -> {
            log.info("CountryNameToCountryCodeTool start with name {} ", name);
            String response;
            List<Country> countries = repository.findByNameContaining(name);
            if (countries.isEmpty()) {

                response = String.format("Error: could not find Country code for provided Country name %s", name);
            } else if (countries.size() != 1) {
                response = String.format("Error: several countries match provided country name %s", name);
            }

            response = countries.getFirst().getCountryCode();
            log.info("CountryNameToCountryCodeTool end with name {} and response {}", name, response);
            return response;
        };
    }
}