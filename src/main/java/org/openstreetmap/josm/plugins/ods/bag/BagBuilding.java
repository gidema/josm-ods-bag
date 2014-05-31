package org.openstreetmap.josm.plugins.ods.bag;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.plugins.ods.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.Entity;

public class BagBuilding extends BagEntity implements Building {
    private Integer bouwjaar;
    private String status;
    private boolean incomplete;
    private String gebruiksdoel;
    private Double oppervlakteMin;
    private Double oppervlakteMax;
    private BagAddress address;
    private Set<AddressNode> addressNodes = new HashSet<>();
    private Long aantalVerblijfsobjecten;
    private String buildingType;
    private boolean underConstruction;
    private String startDate;
    private Set<Building> neighbours = new HashSet<>();
    private City city;
    
    @Override
    public Class<? extends Entity> getType() {
        return Building.class;
    }

    public void setBouwjaar(Integer bouwjaar) {
        this.bouwjaar = bouwjaar;
        if (bouwjaar != null) {
            this.startDate = bouwjaar.toString();
        }
    }

    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setOppervlakteMin(Double oppervlakteMin) {
        this.oppervlakteMin = oppervlakteMin;
    }

    public void setOppervlakteMax(Double oppervlakteMax) {
        this.oppervlakteMax = oppervlakteMax;
    }


    public void setAantalVerblijfsobjecten(Long aantalVerblijfsobjecten) {
        this.aantalVerblijfsobjecten = aantalVerblijfsobjecten;
    }

    public Integer getBouwjaar() {
        return bouwjaar;
    }

    public String getStatus() {
        return status;
    }

    public String getGebruiksdoel() {
        return gebruiksdoel;
    }

    public Double getOppervlakteMin() {
        return oppervlakteMin;
    }

    public Double getOppervlakteMax() {
        return oppervlakteMax;
    }

    public void setUnderConstruction(boolean underConstruction) {
        this.underConstruction = underConstruction;
    }

    public Long getAantal_verblijfsobjecten() {
        return aantalVerblijfsobjecten;
    }

    @Override
    public boolean isUnderConstruction() {
        return underConstruction;
    }

    @Override
    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    @Override
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }
    
    @Override
    public boolean isIncomplete() {
        return incomplete;
    }

    @Override
    public boolean isDeleted() {
        return "Pand gesloopt".equalsIgnoreCase(status);
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasGeometry() {
        return true;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public City getCity() {
        return city;
    }

    public void setAddress(BagAddress address) {
        this.address = address;
    }
    
    @Override
    public BagAddress getAddress() {
        return address;
    }

    @Override
    public Set<AddressNode> getAddressNodes() {
        return addressNodes;
    }

    @Override
    public Set<Building> getNeighbours() {
        return neighbours;
    }

    @Override
    public void addNeighbour(Building building) {
        this.neighbours.add(building);
    }
        
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Building ").append(getReferenceId());
        sb.append(" (").append(getStatus()).append(")");
        for (AddressNode a :addressNodes) {
            sb.append("\n").append(a.toString());
        }
        return sb.toString();
    }
}
