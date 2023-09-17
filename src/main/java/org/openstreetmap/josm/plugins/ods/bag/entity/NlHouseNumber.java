package org.openstreetmap.josm.plugins.ods.bag.entity;

import java.util.Comparator;

public interface NlHouseNumber {
    public static final Comparator<NlHouseNumber> DEFAULT_COMPARATOR = Comparator.nullsFirst(
            Comparator.comparing(NlHouseNumber::getHouseNumber, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(NlHouseNumber::getHouseLetter, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(NlHouseNumber::getHouseNumberExtra, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(NlHouseNumber::getHouseNumberPrefix, Comparator.nullsFirst(Comparator.naturalOrder()))
        );

    /**
     * Some house numbers have a non-numeric prefix.
     * At least in Oude Pekela in The Netherlands
     * 
     * @return The house number prefix
     */
    public String getHouseNumberPrefix();

    /**
     * The house number is the main numeric part of the full house number.
     * This is the only required part of the house number
     * 
     * @return The house number of the address
     */
    public Integer getHouseNumber();

    /**
     * The house letter is the first capital letter directly following
     * the main house number
     * 
     * @return The house letter
     */
    public Character getHouseLetter();

    /**
     * The house number extra is anything behind the main house number
     * (including an optional house letter)
     * 
     * @return The house number extra part
     */
    public String getHouseNumberExtra();
    
    public String getFullHouseNumber();

}
