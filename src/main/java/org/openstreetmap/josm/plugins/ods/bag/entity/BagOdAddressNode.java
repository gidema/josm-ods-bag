package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdAddressNode;

public class BagOdAddressNode extends AbstractOdAddressNode {
    private String gebruiksdoel;
    private Double area;

    @Override
    public NL_Address getAddress() {
        return (NL_Address) super.getAddress();
    }

    public NL_HouseNumber getHouseNumber() {
        return getAddress().getHouseNumber();
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
