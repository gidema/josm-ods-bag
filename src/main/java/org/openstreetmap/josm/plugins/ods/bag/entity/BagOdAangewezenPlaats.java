package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class BagOdAangewezenPlaats extends AbstractOdEntity {
    private OdAddress address;
    private StatusPlaats status;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(OdAddress address) {
        this.address = address;
    }

    public OdAddress getAddress() {
        return address;
    }

    public StatusPlaats getStatus() {
        return status;
    }

    public void setStatus(StatusPlaats status) {
        this.status = status;
    }

    @Override
    public Match<? extends OdEntity, ? extends OsmEntity> getMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" (").append(getStatus()).append(")");
        sb.append("\n").append(address.toString());
        return sb.toString();
    }
}
