package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public class OsmBagLanduseImpl extends AbstractOsmEntity implements OsmBagLanduse {
    private Long bagId;
    private OsmAddress address;
    private OsmMatch<OsmBagLanduse> bagLanduseMatch;

    @Override
    public Long getBagId() {
        return bagId;
    }

    public void setBagId(Long bagId) {
        this.bagId = bagId;
    }

    public void setAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getAddress() {
        return address;
    }

    @Override
    public BuildingMatch getMatch() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BAG static caravan landuse ").append(getBagId());
        return sb.toString();
    }

    @Override
    public void setMatch(OsmMatch<OsmBagLanduse> bagLanduseMatch) {
        this.bagLanduseMatch = bagLanduseMatch;
    }
}
