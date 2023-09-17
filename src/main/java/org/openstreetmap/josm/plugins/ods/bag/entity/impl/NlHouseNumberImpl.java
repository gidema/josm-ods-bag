package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;

public class NlHouseNumberImpl implements NlHouseNumber {
    private final String houseNumberPrefix;
    private final Integer houseNumber;
    private final Character houseLetter;
    private final String houseNumberExtra;
    private final String fullHouseNumber;
    
    public NlHouseNumberImpl(Integer houseNumber, Character houseLetter,
            String houseNumberExtra) {
        this(null, houseNumber, houseLetter, houseNumberExtra);
    }
    
    public NlHouseNumberImpl(String houseNumberPrefix, Integer houseNumber, Character houseLetter,
            String houseNumberExtra) {
        super();
        this.houseNumberPrefix = houseNumberPrefix;
        this.houseNumber = houseNumber;
        this.houseLetter = houseLetter;
        this.houseNumberExtra = houseNumberExtra;
        this.fullHouseNumber = buildFullHouseNumber();
    }

    @Override
    public String getHouseNumberPrefix() {
        return houseNumberPrefix;
    }

    @Override
    public Integer getHouseNumber() {
        return houseNumber;
    }
    
    @Override
    public Character getHouseLetter() {
        return houseLetter;
    }

    @Override
    public String getHouseNumberExtra() {
        return houseNumberExtra;
    }
    
    @Override
    public String getFullHouseNumber() {
        return fullHouseNumber;
    }

    /**
     * Build the FullHousenumber String from the separate components.
     * 
     * @return
     */
    private String buildFullHouseNumber() {
        StringBuilder sb = new StringBuilder(10);
        if (getHouseNumber() != null) {
            sb.append(getHouseNumber());
        }
        if (getHouseLetter() != null) {
            sb.append(getHouseLetter());
        }
        if (getHouseNumberExtra() != null) {
            sb.append("-").append(getHouseNumberExtra());
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(houseNumber, houseLetter, houseNumberExtra,
                houseNumberPrefix);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NlHouseNumberImpl other = (NlHouseNumberImpl) obj;
        return Objects.equals(houseNumber, other.houseNumber)
                && Objects.equals(houseLetter, other.houseLetter)
                && Objects.equals(houseNumberExtra, other.houseNumberExtra)
                && Objects.equals(houseNumberPrefix, other.houseNumberPrefix);
    }

    @Override
    public String toString() {
        return fullHouseNumber.toString();
    }
}
