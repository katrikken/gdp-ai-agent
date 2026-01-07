package com.katrikken.gdpai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PromptDto {
    public String prompt;
    public String model;
}
