package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdAddressNode;

public class BagOdAddressNode extends AbstractOdAddressNode {

    @Override
    public BagOdAddress getAddress() {
        return (BagOdAddress) super.getAddress();
    }

    public String getHuisLetter() {
        return getAddress().getHuisLetter();
    }

    public String getHuisNummerToevoeging() {
        return getAddress().getHuisNummerToevoeging();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddress());
        if (getStatus() != null) {
            sb.append(" (").append(getStatus()).append(")");
        }
        return sb.toString();
    }
}
