package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagWoonplaats;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;

public class BagBuildingImpl extends AbstractOdEntity implements BagBuilding {
    private Long buildingId;
    private Long aantalVerblijfsobjecten;
    private final List<OdAddressNode> addressNodes = new LinkedList<>();
    private BAGBuildingType buildingType = BAGBuildingType.UNCLASSIFIED;
    private String startDate;
    private final Set<BagBuilding> neighbours = new HashSet<>();
    private BagWoonplaats city;
    private BuildingMatch match;
    private final Map<Long, BagBuildingUnit> buildingUnits = new HashMap<>();

    @Override
    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    /**
     * Set the number of building units as reported by the data source.
     * 
     * @param aantalVerblijfsobjecten
     */
    public void setAantalVerblijfsobjecten(Long aantalVerblijfsobjecten) {
        this.aantalVerblijfsobjecten = aantalVerblijfsobjecten;
    }

    /**
     * Get the number of building units (verblijfsobjecten) as reported by the data source.
     * In most cases this number will equal the number of connected building units, but there can be a
     * difference if one or more building units where outside the download area.
     * 
     * @return The reported number of building units
     */
    public Long getAantal_verblijfsobjecten() {
        return aantalVerblijfsobjecten;
    }

    @Override
    public void setStartYear(Integer year) {
        this.startDate = year == null ? null : Objects.toString(year);
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public BAGBuildingType getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(BAGBuildingType buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public BagWoonplaats getCity() {
        return city;
    }

    @Override
    public Map<Long, BagBuildingUnit> getBuildingUnits() {
        return buildingUnits ;
    }

    @Override
    public List<OdAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<BagBuilding> getNeighbours() {
        return neighbours;
    }

    @Override
    public void setMatch(BuildingMatch match) {
        this.match = match;
    }

    @Override
    public BuildingMatch getMatch() {
        return match;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OdBuilding ").append(getBuildingId());
        sb.append(" (").append(getStatus()).append(")");
        for (OdAddressNode a :getAddressNodes()) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean readyForImport() {
        switch (getStatus()) {
        case IN_USE:
        case IN_USE_NOT_MEASURED:
        case CONSTRUCTION:
            return true;
        default:
            return false;
        }
    }
}
