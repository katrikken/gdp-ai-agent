package com.katrikken.gdpai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/**
 * JPA Entity mapping to the GDP_PER_CAPITA_VIEW.
 * Views should be treated as read-only, hence the @Immutable annotation.
 * <p>
 * The primary key is the composite key (COUNTRY_CODE, DATA_YEAR).
 * The value column (GDP_PER_CAPITA) is the result of the division, mapped to Double.
 */
@Entity
@Data
@Table(name = "GDP_PER_CAPITA_VIEW")
@Immutable
public class GdpPerCapita {

    @EmbeddedId
    private CountryYearId id;

    @Column(name = "COUNTRY_NAME")
    private String name;

    @Column(name = "GDP_PER_CAPITA", nullable = false)
    private BigDecimal gdpPerCapita;
}
