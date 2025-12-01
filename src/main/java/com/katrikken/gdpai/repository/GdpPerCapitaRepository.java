package com.katrikken.gdpai.repository;

import com.katrikken.gdpai.entity.CountryYearId;
import com.katrikken.gdpai.entity.GdpPerCapita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GdpPerCapitaRepository extends JpaRepository<GdpPerCapita, CountryYearId> {

    /**
     * Retrieves all GDP per capita records for a specific country code.
     *
     * @param countryCode The 3-letter country code (e.g., "USA").
     * @return A list of GdpPerCapita records for that country.
     */
    List<GdpPerCapita> findByIdCountryCodeOrderByIdDataYear(String countryCode);

    /**
     * Retrieves all GDP per capita records for a specific year.
     *
     * @param year The data year (e.g., 2023).
     * @return A list of GdpPerCapita records for that year.
     */
    List<GdpPerCapita> findByIdDataYearOrderByIdCountryCode(int year);

    /**
     * Retrieves all GDP per capita records within a specified year range (inclusive).
     *
     * @param startYear The starting year of the range.
     * @param endYear   The ending year of the range.
     * @return A list of GdpPerCapita records within the year range.
     */
    List<GdpPerCapita> findByIdDataYearBetweenOrderByIdCountryCode(int startYear, int endYear);

    /**
     * Retrieves all GDP per capita records within a specified year range (inclusive) for a country.
     *
     * @param countryCode The 3-letter country code (e.g., "USA").
     * @param startYear   The starting year of the range.
     * @param endYear     The ending year of the range.
     * @return A list of GdpPerCapita records within the year range.
     */
    List<GdpPerCapita> findByIdCountryCodeAndIdDataYearBetweenOrderByIdDataYear(String countryCode, int startYear, int endYear);

}
