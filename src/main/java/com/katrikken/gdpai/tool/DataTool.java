package com.katrikken.gdpai.tool;

import com.katrikken.gdpai.entity.CountryYearId;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Description;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Log4j2
public class DataTool {

    @Tool(description = "Get the current year in the user's timezone")
    public String getCurrentYearTool() {
        log.info("getCurrentYearTool called");
        return String.valueOf(LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).getYear());
    }

    @Tool(description = "Log thought process tool")
    public void logTool(String thoughts) {
        log.info("logTool called");
        log.info("AI thoughts: {}", thoughts);
    }

    protected <T> String buildTrendForCountry(
            String heading,
            List<T> items,
            Function<T, CountryYearId> idExtractor,
            Function<T, BigDecimal> valueExtractor,
            String countryCode) {

        if (items == null || items.isEmpty()) {
            return String.format("No %s data available for country code %s.", heading.toLowerCase(), countryCode);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(heading).append(" ").append(countryCode).append(":\n");

        BigDecimal prev = null;
        for (T item : items) {
            CountryYearId id = idExtractor.apply(item);
            BigDecimal current = valueExtractor.apply(item);
            composeTrendString(current, prev, sb, id);
            prev = current;
        }

        return sb.toString().trim();
    }

    private void composeTrendString(BigDecimal currentValue, BigDecimal prevValue, StringBuilder sb, CountryYearId id) {
        String currentStr = currentValue == null ? "null" : currentValue.toPlainString();

        sb.append(id.getDataYear()).append(": ").append(currentStr);

        if (prevValue != null && currentValue != null && prevValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal diff = currentValue.subtract(prevValue);
            String diffSign = diff.signum() >= 0 ? "+" : "-";
            String diffStr = diffSign + diff.abs().toPlainString();

            BigDecimal pct = diff.multiply(BigDecimal.valueOf(100))
                    .divide(prevValue, 6, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);

            String pctStr = diffSign + pct.abs().toPlainString() + "%";

            sb.append(", ").append(diffStr).append(", ").append(pctStr);
        } else if (prevValue != null) {
            // previous exists but cannot compute percent (current or previous null, or previous is zero)
            BigDecimal diff = (currentValue == null) ? null : currentValue.subtract(prevValue);
            String diffStr = diff == null ? "N/A" : (diff.signum() >= 0 ? "+" : "-") + diff.abs().toPlainString();
            sb.append(", ").append(diffStr).append(", N/A");
        }

        sb.append("\n");
    }


    /**
     * Input structure for querying by country code.
     */
    public record CountryCodeQuery(
            @Description("The 3-letter uppercase country code (e.g., USA, CAN, FRA). " +
                    "Must be strictly 3 letters, use countryNameToCountryCodeTool tool to get it from country name")
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
     * Input structure for querying by a range of years and country code.
     */
    public record CountryCodeYearRangeQuery(
            @Description("The 3-letter uppercase country code (e.g., USA, CAN, FRA). " +
                    "Must be strictly 3 letters, use countryNameToCountryCodeTool tool to get it from country name")
            String countryCode,
            @Description("The inclusive starting year of the range (e.g., 2021).")
            int startYear,
            @Description("The inclusive ending year of the range (e.g., 2023).")
            int endYear) {
    }


    /**
     * Record defining the required input for the GDP and Population tool (Country Code and Year).
     */
    public record CountryCodeYearQuery(
            @Description("The 3-letter uppercase country code (e.g., USA, CAN, FRA). " +
                    "Must be strictly 3 letters, use countryNameToCountryCodeTool tool to get it from country name")
            String countryCode,
            @Description("The specific year of the data (e.g., 2023). Must be a four-digit number.")
            int year) {
    }
}
