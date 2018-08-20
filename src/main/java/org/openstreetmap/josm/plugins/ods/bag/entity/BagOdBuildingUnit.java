package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.DefaultOdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class BagOdBuildingUnit extends DefaultOdBuildingUnit {
    private Long buildingUnitId;
    private Double area;
    private final ZeroOneMany<OdBuilding> buildings = new ZeroOneMany<>();

    @Override
    public Match<? extends OdEntity, ? extends OsmEntity> getMatch() {
        // The BuildingUnit entity type doesn't have an OSM equivalent, so matching is not applicable.
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getBuildingUnitId() {
        return buildingUnitId;
    }

    @Override
    public void setBuildingUnitId(Long id) {
        this.buildingUnitId = id;
    }

    @Override
    public void addBuilding(OdBuilding building) {
        buildings.add(building);
    }

    @Override
    public ZeroOneMany<OdBuilding> getBuildings() {
        return buildings;
    }

    @Override
    public void setArea(Double area) {
        this.area = area;
    }

    @Override
    public Double getArea() {
        return area;
    }
}
