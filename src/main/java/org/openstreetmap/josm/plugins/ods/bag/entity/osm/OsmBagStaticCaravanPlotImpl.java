package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatch;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public class OsmBagStaticCaravanPlotImpl extends AbstractOsmEntity implements OsmBagStaticCaravanPlot {
    private Long bagId;
    private OsmAddress address;
    private OsmMatch<OsmBagStaticCaravanPlot> bagLanduseMatch;

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
    public OsmAddress getMainAddress() {
        return address;
    }

    @Override
    public BuildingMatch getMatch() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setMatch(OsmMatch<OsmBagStaticCaravanPlot> bagLanduseMatch) {
        this.bagLanduseMatch = bagLanduseMatch;
    }

    @Override
    public AddressableObjectStatus getAddressableStatus() {
        return AddressableObjectStatus.IN_USE;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BAG static caravan plot ").append(getBagId());
        return sb.toString();
    }
}
