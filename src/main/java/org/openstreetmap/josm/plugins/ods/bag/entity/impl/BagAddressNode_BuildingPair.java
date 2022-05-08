package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

public class BagAddressNode_BuildingPair {
    private Long buildingUnitId;
    private Long buildingId;
    private Long[] primaryId;

    
    public BagAddressNode_BuildingPair(Long buildingUnitId, Long buildingId) {
        super();
        this.buildingUnitId = buildingUnitId;
        this.buildingId = buildingId;
        this.primaryId = new Long[] {buildingUnitId, buildingId};
    }

    public Long[] getPrimaryId() {
        return primaryId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public Long getBuildingUnitId() {
        return buildingUnitId;
    }
}
