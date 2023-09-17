package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.bag.mapping.BuildingMapping;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Building extends Entity {
    public String getStartDate();

    public BuildingType getBuildingType();

    public void setMatch(BuildingMapping match);

    @Override
    public BuildingMapping getMapping();

    // Setters
    public void setBuildingType(BuildingType buildingType);
}
