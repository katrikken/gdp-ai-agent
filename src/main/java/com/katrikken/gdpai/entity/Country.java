package com.katrikken.gdpai.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COUNTRY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @Column(name = "COUNTRY_CODE", length = 3, nullable = false)
    private String countryCode;

    @Column(name = "NAME", length = 60, nullable = false)
    private String name;

    @Column(name = "REGION", length = 40)
    private String region;

    @Column(name = "INCOME_GROUP", length = 40)
    private String incomeGroup;

    @Lob
    @Column(name = "SPECIAL_NOTES")
    private String specialNotes;
}
