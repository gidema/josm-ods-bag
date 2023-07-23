package org.openstreetmap.josm.plugins.ods.bag.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface BagBuilding extends OdEntity {

    public void setMatch(BuildingMatch match);

    public Long getBuildingId();

    // Setters
    public void setBuildingType(BAGBuildingType buildingType);

    @Override
    public Geometry getGeometry();

    public BagWoonplaats getCity();


    /**
     * Return the address nodes associated with this building.
     *
     * @return empty collection if no address nodes are associated with this
     *         building.
     */
    public List<OdAddressNode> getAddressNodes();

    public Map<Long, BagBuildingUnit> getBuildingUnits();

    public Set<BagBuilding> getNeighbours();

    public void setStartYear(Integer year);

    public String getStartDate();

    public BuildingStatus getStatus();
    
    public void setStatus(BuildingStatus status);
    
    public BAGBuildingType getBuildingType();

    @Override
    public BuildingMatch getMatch();
}
