package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface BagBuildingUnit extends OdEntity {
    public Long getBuildingUnitId();
    
    @Override
    public AddressNodeMatch getMatch();

    public OdAddressNode getMainAddressNode();
    
//    public NLAddress getAddress();

    public BagBuilding getBuilding();

    public void setBuilding(BagBuilding building);

    public void setMatch(AddressNodeMatch match);

    public String getGebruiksdoel();

    public double getArea();

    public BuildingUnitStatus getStatus();
}
