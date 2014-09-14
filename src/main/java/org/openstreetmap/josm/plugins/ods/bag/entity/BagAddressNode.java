package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNodeImpl;

public class BagAddressNode extends AddressNodeImpl {
    private String gebruiksdoel;
    private String status;
    private Double area;
    private BagAddress bagAddress;
    
    
    public BagAddressNode(BagAddress address) {
        super(address);
        this.bagAddress = address;
    }

    @Override
    public boolean isIncomplete() {
        if (getBuilding() != null) {
            return getBuilding().isIncomplete();
        }
        return super.isIncomplete();
    }
    
    public String getHuisLetter() {
        return bagAddress.getHuisLetter();
    }

    public String getHuisNummerToevoeging() {
        return bagAddress.getHuisNummerToevoeging();
    }


//    @Override
//    public Block getBlock() {
//        // TODO Auto-generated method stub
//        return null;
//    }

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
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Address o) {
        return bagAddress.compareTo(o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bagAddress).append(" (");
        sb.append(getStatus()).append(")");
        return sb.toString();
    }
}
