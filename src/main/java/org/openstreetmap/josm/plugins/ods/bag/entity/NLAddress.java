package org.openstreetmap.josm.plugins.ods.bag.entity;

/**
 * Although not supported in the official BAG administration, some house numbers have a non-numeric prefix.
 * These prefixes are used 
 * 
 * @author Idema
 *
 */
public interface NLAddress {
    /**
     * Get the house number
     * @see NlHouseNumber
     */
    public NlHouseNumber getHouseNumber();

    public String getStreetName();

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
    public String getPostcode();

    public String getCityName();


}
