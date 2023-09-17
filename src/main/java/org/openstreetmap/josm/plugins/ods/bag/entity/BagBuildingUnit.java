package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.bag.mapping.AddressNodeMapping;

public interface BagBuildingUnit extends BagAddressableObject {
    
    @Override
    public AddressNodeMapping getMapping();

    public OdAddressNode getMainAddressNode();
    
//    public NLAddress getAddress();

    public BagBuilding getBuilding();

    public void setBuilding(BagBuilding building);

    public void setMatch(AddressNodeMapping match);

    public String getGebruiksdoel();

    public double getArea();

    public BuildingUnitStatus getStatus();
}
