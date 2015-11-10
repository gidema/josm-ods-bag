package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.apache.commons.lang.ObjectUtils;
import org.openstreetmap.josm.plugins.ods.entities.actual.Address;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressImpl;

public class BagAddress extends AddressImpl {
    private String huisletter;
    private String huisnummertoevoeging;

    public void setHuisletter(String huisletter) {
        this.huisletter = huisletter;
    }

    public String getHuisLetter() {
        return huisletter;
    }

    public void setHuisnummerToevoeging(String toevoeging) {
        this.huisnummertoevoeging = toevoeging;
    }

    public String getHuisNummerToevoeging() {
        return huisnummertoevoeging;
    }
    
    @Override
    public String formatHouseNumber() {
        StringBuilder sb = new StringBuilder(10);
        sb.append(getHouseNumber());
        if (getHuisLetter() != null) {
            sb.append(getHuisLetter());
        }
        if (getHuisNummerToevoeging() != null) {
            sb.append('-').append(getHuisNummerToevoeging());
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Address a) {
        int result = ObjectUtils.compare(getCityName(), a.getCityName());
        if (result != 0) return result;
        result = ObjectUtils.compare(getPostcode(), a.getPostcode());
        if (result != 0) return result;
        result = ObjectUtils.compare(getStreetName(), a.getStreetName());
        if (result != 0) return result;
        return getFullHouseNumber().compareTo(a.getFullHouseNumber());
    }
}
