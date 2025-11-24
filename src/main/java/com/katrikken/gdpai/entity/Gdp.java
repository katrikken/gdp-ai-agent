package com.katrikken.gdpai.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "GDP")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gdp {

    // Uses the composite key defined in TimeSeriesId
    @EmbeddedId
    private CountryYearId id;

    private BigDecimal gdp;
}
