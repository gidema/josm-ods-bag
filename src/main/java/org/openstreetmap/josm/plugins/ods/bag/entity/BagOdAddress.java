package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdAddress;

public class BagOdAddress extends AbstractOdAddress {
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
}
