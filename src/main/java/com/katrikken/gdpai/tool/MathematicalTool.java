package com.katrikken.gdpai.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Description;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Log4j2
public class MathematicalTool {


    public static final String GET_CURRENT_YEAR_DESCRIPTION = "Get the current year in the user's timezone";

    public static final String OPERATE_ON_BIG_DECIMALS_DESCRIPTION = "Takes a BigDecimalValuesQuery record and performs operations like (first - second).";

    @Tool(description = GET_CURRENT_YEAR_DESCRIPTION)
    public String getCurrentYearTool() {
        log.info("getCurrentYearTool called");
        return String.valueOf(LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).getYear());
    }

    @Tool(description = OPERATE_ON_BIG_DECIMALS_DESCRIPTION)
    public String operateOnBigDecimals(BigDecimalValuesQuery query) {
        log.info("operateOnBigDecimals called with query: " + query);
        if (query.operation.equals("+")) {
            return query.first.add(query.second).toString();
        } else if (query.operation.equals("-")) {
            return query.first.add(query.second.negate()).toString();
        } else if (query.operation.equals("*")) {
            return query.first.multiply(query.second).toString();
        } else if (query.operation.equals("/")) {
            return query.first.divide(query.second, RoundingMode.UNNECESSARY).toString();
        } else if (query.operation.equals("compare")) {
            return (query.first.compareTo(query.second)) + "";
        } else {
            return "Error: unknown operation " + query.operation;
        }
    }

    /**
     * Input structure for querying mathematical operations on BigDecimal values
     */
    public record BigDecimalValuesQuery(
            @Description("The first BigDecimal value operand")
            BigDecimal first,
            @Description("The second BigDecimal value operand")
            BigDecimal second,
            @Description("One of the following operations: '+', '-', '/', '*' or 'compare', where comparison returns" +
                    " -1 if the first value is lower, 0 if they are equal or 1 if the first value is bigger.")
            String operation) {
    }

}
