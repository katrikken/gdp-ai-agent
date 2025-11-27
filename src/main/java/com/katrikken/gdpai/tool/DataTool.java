package com.katrikken.gdpai.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Description;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;

@Log4j2
public class DataTool {

    @Tool(description = "Get the current year in the user's timezone")
    public String getCurrentYearTool() {
        log.info("getCurrentYearTool called");
        return String.valueOf(LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).getYear());
    }

    /**
     * Input structure for querying by country code.
     */
    public record CountryCodeQuery(
            @Description("The 3-letter uppercase country code (e.g., USA, CAN, FRA).")
            String countryCode) {
    }

    /**
     * Input structure for querying by a single year.
     */
    public record YearQuery(
            @Description("The specific year of the data (e.g., 2023). Must be a four-digit number.")
            int year) {
    }

    /**
     * Input structure for querying by a range of years.
     */
    public record YearRangeQuery(
            @Description("The inclusive starting year of the range (e.g., 2021).")
            int startYear,
            @Description("The inclusive ending year of the range (e.g., 2023).")
            int endYear) {
    }


    /**
     * Record defining the required input for the GDP and Population tool (Country Code and Year).
     */
    public record CountryCodeYearQuery(
            @Description("The 3-letter uppercase country code (e.g., USA, CAN, FRA).")
            String countryCode,
            @Description("The specific year of the data (e.g., 2023). Must be a four-digit number.")
            int year) {
    }
}
