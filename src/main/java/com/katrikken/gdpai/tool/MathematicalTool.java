package com.katrikken.gdpai.tool;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

@Log4j2
public class MathematicalTool {

    @Tool(description = "Takes a IntegerValuesQuery record and " +
            "performs operation (first 'operator' second).")
    public Function<IntegerValuesQuery, String> operateOnIntegers() {
        return (IntegerValuesQuery query) -> {
            log.info("operateOnIntegers called with query: " + query);
            if (query.operation.equals("+")) {
                return (query.first + query.second) + "";
            } else if (query.operation.equals("-")) {
                return (query.first - query.second) + "";
            } else if (query.operation.equals("*")) {
                return (query.first * query.second + "");
            } else if (query.operation.equals("/")) {
                return (query.first / query.second) + "";
            } else if (query.operation.equals("compare")) {
                return (query.first.compareTo(query.second)) + "";
            } else {
                return "Error: unknown operation " + query.operation;
            }
        };
    }

    @Tool(description = "Takes a LongValuesQuery record and " +
            "performs operation (first 'operator' second).")
    public Function<LongValuesQuery, String> operateOnLongs() {
        return (LongValuesQuery query) -> {
            log.info("operateOnLongs called with query: " + query);
            if (query.operation.equals("+")) {
                return (query.first + query.second) + "";
            } else if (query.operation.equals("-")) {
                return (query.first - query.second) + "";
            } else if (query.operation.equals("*")) {
                return (query.first * query.second + "");
            } else if (query.operation.equals("/")) {
                return (query.first / query.second) + "";
            } else if (query.operation.equals("compare")) {
                return (query.first.compareTo(query.second)) + "";
            } else {
                return "Error: unknown operation " + query.operation;
            }
        };
    }

    @Tool(description = "Takes a BigDecimalValuesQuery record and " +
            "performs operation (first 'operator' second).")
    public Function<BigDecimalValuesQuery, String> operateOnBigDecimals() {
        return (BigDecimalValuesQuery query) -> {
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
        };
    }

    /**
     * Input structure for querying mathematical operations on Integer values
     */
    public record IntegerValuesQuery(
            @Description("The first Integer value operand")
            Integer first,
            @Description("The second Integer value operand")
            Integer second,
            @Description("One of the following operations: '+', '-', '/', '*' or 'compare', where comparison returns" +
                    " -1 if the first value is lower, 0 if they are equal or 1 if the first value is bigger.")
            String operation) {
    }

    /**
     * Input structure for querying mathematical operations on Long values
     */
    public record LongValuesQuery(
            @Description("The first Long value operand")
            Long first,
            @Description("The second Long value operand")
            Long second,
            @Description("One of the following operations: '+', '-', '/', '*' or 'compare', where comparison returns" +
                    " -1 if the first value is lower, 0 if they are equal or 1 if the first value is bigger.")
            String operation) {
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
