package com.katrikken.gdpai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CountryYearId {
    @Column(name = "COUNTRY_CODE", length = 3, nullable = false)
    private String countryCode;

    @Column(name = "DATA_YEAR", nullable = false)
    private int dataYear;
}
