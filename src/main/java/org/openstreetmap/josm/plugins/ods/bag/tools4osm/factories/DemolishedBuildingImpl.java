package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagWoonplaats;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.DemolishedBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;

public class DemolishedBuildingImpl extends AbstractOdEntity implements DemolishedBuilding {
    private Long buildingId;

    public DemolishedBuildingImpl() {
        super();
    }

    @Override
    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long id) {
        this.buildingId = id;
    }

    @Override
    public boolean readyForImport() {
        return false;
    }

    @Override
    public String getStatusTag() {
        return "DEMOLISHED";
    }

    @Override
    public void setBuildingType(BAGBuildingType buildingType) {
        // Leave empty
    }

    @Override
    public BagWoonplaats getCity() {
        return null;
    }

    @Override
    public List<OdAddressNode> getAddressNodes() {
        return null;
    }

    @Override
    public Map<Long, BagBuildingUnit> getBuildingUnits() {
        return null;
    }

    @Override
    public Set<BagBuilding> getNeighbours() {
        return null;
    }

    @Override
    public void setStartYear(Integer year) {
        // Leave empty
    }

    @Override
    public Integer getStartYear() {
        return null;
    }

    @Override
    public BuildingStatus getStatus() {
        return BuildingStatus.REMOVED;
    }

    @Override
    public void setStatus(BuildingStatus status) {
        // Leave empty
    }

    @Override
    public BAGBuildingType getBuildingType() {
        return null;
    }

    @Override
    public double getAge() {
        return 0;
    }

    @Override
    public UpdateTaskType getUpdateTaskType() {
        if (!getMapping().getOsmEntities().isEmpty() && !isUpdated()) {
            return UpdateTaskType.DELETE;
        }
        return UpdateTaskType.NONE;
    }
}
