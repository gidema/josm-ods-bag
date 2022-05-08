package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

public class BagOsmAddress extends AbstractOsmAddress {
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
}
