package org.openstreetmap.josm.plugins.ods.bag.entity;

/**
 * This class represents a street address.
 * Although originally intended to be general-purpose, the current
 * interface is especially targeting addresses in The Netherlands.
 * 
 * @author gertjan
 *
 */
public interface Address {
    /**
     * House numbers tend to be more complex than just an integer.
     * Therefore they have their own interface which can have a country-specific
     * implementation;
     * 
     * @return The house number
     */
    public NlHouseNumber getHouseNumber();

    public String getStreetName();

    public String getPostcode();

    public String getCityName();
}
