package org.openstreetmap.josm.plugins.ods.bag.relations;

import java.util.Objects;

public class Building_BuildingUnitPair {
    private final Long buildingId;
    private final Long buildingUnitId;

    public Building_BuildingUnitPair(Long buildingId, Long buildingUnitId) {
        super();
        this.buildingId = buildingId;
        this.buildingUnitId = buildingUnitId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public Long getBuildingUnitId() {
        return buildingUnitId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(buildingId, buildingUnitId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Building_BuildingUnitPair)) {
            return false;
        }
        Building_BuildingUnitPair other = (Building_BuildingUnitPair) obj;
        return Objects.equals(other.buildingUnitId, this.buildingUnitId) &&
                Objects.equals(other.buildingId, this.buildingId);
    }
}
