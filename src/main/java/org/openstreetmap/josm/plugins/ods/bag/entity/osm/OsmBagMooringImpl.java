package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public class OsmBagMooringImpl extends AbstractOsmEntity implements OsmBagMooring {
    private Long mooringId;
    private OsmAddress address;
    private OsmMatch<OsmBagMooring> mooringMatch;

    @Override
    public Long getMooringId() {
        return mooringId;
    }

    public void setMooringId(Long mooringId) {
        this.mooringId = mooringId;
    }

    public void setAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getMainAddress() {
        return address;
    }

    @Override
    public BuildingMatch getMatch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMatch(OsmMatch<OsmBagMooring> mooringMatch) {
        this.mooringMatch = mooringMatch;
    }

    @Override
    public AddressableObjectStatus getAddressableStatus() {
        return AddressableObjectStatus.IN_USE;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BAG mooring plot ").append(getMooringId());
        return sb.toString();
    }
}
