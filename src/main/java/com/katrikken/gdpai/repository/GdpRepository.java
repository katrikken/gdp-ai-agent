package com.katrikken.gdpai.repository;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.Gdp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GdpRepository extends JpaRepository<Gdp, CountryYearId> {

    /**
     * Finds all GDP data records for a specific country code.
     */
    List<Gdp> findByIdCountryCode(String countryCode);

    /**
     * Finds all GDP data for a specific year.
     */
    List<Gdp> findByIdDataYear(int dataYear);

    /**
     * Finds a specific record by country and year.
     */
    Gdp findByIdCountryCodeAndIdDataYear(String countryCode, int dataYear);
}
