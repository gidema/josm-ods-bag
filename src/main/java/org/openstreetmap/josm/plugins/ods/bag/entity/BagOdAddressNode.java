package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdAddressNode;

public class BagOdAddressNode extends AbstractOdAddressNode {
    private String gebruiksdoel;
    private Double area;

    @Override
    public BagOdAddress getAddress() {
        return (BagOdAddress) super.getAddress();
    }

    @Override
    public boolean isIncomplete() {
        if (getBuilding() != null) {
            return getBuilding().isIncomplete();
        }
        return super.isIncomplete();
    }

    public String getHuisLetter() {
        return getAddress().getHuisLetter();
    }

    public String getHuisNummerToevoeging() {
        return getAddress().getHuisNummerToevoeging();
    }

    public String getGebruiksdoel() {
        return gebruiksdoel;
    }

    public void setGebruiksdoel(String gebruiksdoel) {
        this.gebruiksdoel = gebruiksdoel;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public double getArea() {
        return area;
    }

    //    @Override
    //    public int compareTo(Address o) {
    //        return getAddress().compareTo(o);
    //    }

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
