package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public interface Building extends Entity {
    public String getStartDate();

    public BuildingType getBuildingType();

    public void setMatch(BuildingMatch match);

    @Override
    public BuildingMatch getMatch();

    // Setters
    public void setBuildingType(BuildingType buildingType);
}
