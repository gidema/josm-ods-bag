package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingImpl;

public class BagBuilding extends BuildingImpl {
    private Long aantalVerblijfsobjecten;
    private BagAddress bagAddress;
    
    public void setAddress(BagAddress address) {
        super.setAddress(address);
        this.bagAddress = address;
    }
    
    @Override
    public BagAddress getAddress() {
        return bagAddress;
    }
    
    public void setAantalVerblijfsobjecten(Long aantalVerblijfsobjecten) {
        this.aantalVerblijfsobjecten = aantalVerblijfsobjecten;
    }

    public Long getAantal_verblijfsobjecten() {
        return aantalVerblijfsobjecten;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ").append(getReferenceId());
        sb.append(" (").append(getStatus()).append(")");
        for (AddressNode a :getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
