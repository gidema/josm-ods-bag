package org.openstreetmap.josm.plugins.ods.bag.relations;

import java.util.Objects;

public class BuildingUnit_AddressNodePair {
    private final Long buildingUnitId;
    private final Long addressNodeId;

    public BuildingUnit_AddressNodePair(Long buildingUnitId, Long addressNodeId) {
        super();
        this.buildingUnitId = buildingUnitId;
        this.addressNodeId = addressNodeId;
    }

    public Long getBuildingUnitId() {
        return buildingUnitId;
    }

    public Long getAddressNodeId() {
        return addressNodeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildingUnitId, addressNodeId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BuildingUnit_AddressNodePair)) {
            return false;
        }
        BuildingUnit_AddressNodePair other = (BuildingUnit_AddressNodePair) obj;
        return Objects.equals(other.buildingUnitId, this.buildingUnitId) &&
                Objects.equals(other.addressNodeId, this.addressNodeId);
    }
}
