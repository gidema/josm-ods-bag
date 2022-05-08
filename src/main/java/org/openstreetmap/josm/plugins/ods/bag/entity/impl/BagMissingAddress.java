package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

public class BagMissingAddress extends BagOdAddressNode {
    private Long buildingUnitId;
    
    @Override
    public Completeness getCompleteness() {
        return Completeness.Complete;
    }
    
    public void setBuildingUnitId(Long buildingUnitId) {
        this.buildingUnitId = buildingUnitId;
    }

    public Long getBuildingUnitId() {
        return buildingUnitId;
    }
}
