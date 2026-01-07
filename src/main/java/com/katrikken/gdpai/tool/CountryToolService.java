package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.Country;
import com.katrikken.gdpai.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CountryToolService extends DataTool {

    private final static String COUNTRY_NAME_TO_CODE_DESCRIPTION = "Converts Country name to Country code for querying data in the database. " +
            "Input is a CountryQuery, output is the corresponding 3 letter Country code string. " +
            "If no matching Country is found, return an error message. " +
            "If multiple Countries match the provided name, return an error message.";

    private final CountryRepository repository;

    /**
     * AI Tool: Converts Country name to Country code for querying data in the database
     *
     * @return a Function
     */
    @Tool(description = COUNTRY_NAME_TO_CODE_DESCRIPTION)
    public String countryNameToCountryCodeTool(CountryQuery name) {
        log.info("CountryNameToCountryCodeTool start with name {} ", name);
        String response;
        List<Country> countries = repository.findByNameContaining(name.countryName());
        if (countries.isEmpty()) {
            response = String.format("Error: could not find Country code for provided Country name %s. " +
                    "Some countries have several widely used names, try a different one", name.countryName());
        } else if (countries.size() != 1) {
            response = String.format("Error: several countries match provided country name %s", name.countryName());
        } else {
            response = countries.getFirst().getCountryCode();
        }
        log.info("CountryNameToCountryCodeTool end with name {} and response {}", name.countryName(), response);
        return response;
    }
}