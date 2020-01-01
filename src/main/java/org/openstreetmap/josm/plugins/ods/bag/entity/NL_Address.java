package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

/**
 * Addresses in The Netherlands (the European part to be precise), are largely
 * Standardised in the BAG administration (Base administration for addresses
 * and buildings).
 * The addresses consist of 4 parts: cityName, streetName, postcode and house number.
 * 
 * Any combination postcode and house number is unique in The Netherlands.
 * The means that when you have these 2, you can defer streetName, city,
 * municipality etc. Not all addresses have a postcode though.
 * 
 * @author gertjan
 *
 */
public class NL_Address implements OdAddress {
    private NL_HouseNumber houseNumber;
    private String postcode;
    private String streetName;
    private String cityName;

    /**
     * Get the house number
     * @see NL_HouseNumber
     */
    public NL_HouseNumber getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(NL_HouseNumber houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Override
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * Get the postal code.
     * 
     * Postal codes consist of 4 digits and 2 Capital letters. In print, the digits
     * and letters are separated with a space. In the BAG administration, the space
     * is omitted. The space is typically omitted in OSM as well, but because 
     * mappers may add it, any code handling postcodes should be capable to
     * deal with the it.
     * The postal code may be null. This is typically the case for addresses on
     * buildings that don't have a mailbox like garages and transformer stations.
     */
    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public String getCityName() {
        return cityName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getStreetName()).append(" ");
        sb.append(getHouseNumber());
        if (getPostcode() != null) {
            sb.append(' ').append(getPostcode());
        }
        if (getCityName() != null) {
            sb.append(' ').append(getCityName());
        }
        return sb.toString();
    }
}
