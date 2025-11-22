package com.katrikken.gdpai.repository;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Population;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopulationRepository extends JpaRepository<Population, CountryYearId> {

    /**
     * Finds all population data records for a specific country code.
     */
    List<Population> findByIdCountryCode(String countryCode);

    /**
     * Finds all population data for a specific year.
     */
    List<Population> findByIdDataYear(int year);

    /**
     * Finds a specific record by country and year.
     */
    Population findByIdCountryCodeAndIdDataYear(String countryCode, int year);
}
