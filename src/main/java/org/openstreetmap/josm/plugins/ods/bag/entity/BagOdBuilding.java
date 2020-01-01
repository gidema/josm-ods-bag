package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.AbstractOdBuilding;

public class BagOdBuilding extends AbstractOdBuilding {
    private Long aantalVerblijfsobjecten;

//    public void setAddress(NL_Address address) {
//        super.setAddress(address);
//        this.bagOdAddress = address;
//    }

//    @Override
//    public NL_Address getAddress() {
//        return super.getAddress();
//    }

    public void setAantalVerblijfsobjecten(Long aantalVerblijfsobjecten) {
        this.aantalVerblijfsobjecten = aantalVerblijfsobjecten;
    }

    public Long getAantal_verblijfsobjecten() {
        return aantalVerblijfsobjecten;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OdBuilding ").append(getReferenceId());
        sb.append(" (").append(getStatus()).append(")");
        for (OdAddressNode a :getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
