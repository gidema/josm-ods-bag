package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdAddress;

public class BagOdAddress extends AbstractOdAddress {
    private NL_HouseNumber houseNumber;

    public NL_HouseNumber getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(NL_HouseNumber houseNumber) {
        this.houseNumber = houseNumber;
    }
}
