package com.katrikken.gdpai.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GDP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gdp {

    // Uses the composite key defined in TimeSeriesId
    @EmbeddedId
    private CountryYearId id;

    private Long gdp;
}
