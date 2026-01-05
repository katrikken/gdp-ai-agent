package com.katrikken.gdpai.tool.ollama;

import com.katrikken.gdpai.tool.MathematicalTool;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class OllamaMathematicalTool {

    private final MathematicalTool mathematicalTool = new MathematicalTool();

    @Bean
    @Description(MathematicalTool.GET_CURRENT_YEAR_DESCRIPTION)
    public Supplier<String> getCurrentYear() {
        return mathematicalTool::getCurrentYearTool;
    }

    @Bean
    @Description(MathematicalTool.OPERATE_ON_BIG_DECIMALS_DESCRIPTION)
    public Function<MathematicalTool.BigDecimalValuesQuery, String> operateOnBigDecimals() {
        return mathematicalTool::operateOnBigDecimals;
    }
}
