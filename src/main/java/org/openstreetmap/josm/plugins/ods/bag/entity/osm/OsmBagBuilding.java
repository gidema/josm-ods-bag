package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingType;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public class OsmBagBuilding extends AbstractOsmEntity implements OsmBuilding {
    private Long buildingId;
    private OsmAddress address;
    private final List<OsmAddressNode> addressNodes = new LinkedList<>();
    private BuildingType buildingType = BuildingType.UNCLASSIFIED;
    private String startDate;
    private final Set<OsmBuilding> neighbours = new HashSet<>();
    private OsmCity city;
    private BuildingStatus status;
    private OsmMatch<OsmBuilding> buildingMatch;

    @Override
    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    @Override
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public BuildingType getBuildingType() {
        return buildingType;
    }

    @Override
    public void setBuildingType(BuildingType buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public OsmCity getCity() {
        return city;
    }

    public void setMainAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getMainAddress() {
        return address;
    }

    @Override
    public List<OsmAddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<OsmBuilding> getNeighbours() {
        return neighbours;
    }

    @Override
    public BuildingStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(BuildingStatus status) {
        this.status = status;
    }
    @Override
    public AddressableObjectStatus getAddressableStatus() {
        switch (getStatus()) {
        case CONSTRUCTION:
            return AddressableObjectStatus.CONSTRUCTION;
        case IN_USE:
            return AddressableObjectStatus.IN_USE;
        case PLANNED:
            return AddressableObjectStatus.PLANNED;
        case REMOVAL_DUE:
            return AddressableObjectStatus.REMOVAL_DUE;
        default:
            return AddressableObjectStatus.UNKNOWN;
        }
    }

    @Override
    public void setMatch(BuildingMatch buildingMatch) {
        this.buildingMatch = buildingMatch;
    }

    @Override
    public BuildingMatch getMatch() {
        return (BuildingMatch) buildingMatch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("OsmBuilding ").append(getBuildingId());
        for (OsmAddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }

    @Override
    public void setMatch(OsmMatch<OsmBuilding> buildingMatch) {
        this.buildingMatch = buildingMatch;
    }
}
