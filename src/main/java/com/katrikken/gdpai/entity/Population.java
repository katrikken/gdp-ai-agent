package com.katrikken.gdpai.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "POPULATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Population {

    @EmbeddedId
    private CountryYearId id;

    private Long population;
}
