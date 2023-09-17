package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;

public class OsmBagStaticCaravanPlotImpl extends AbstractOsmEntity implements OsmBagStaticCaravanPlot {
    private Long bagId;
    private OsmAddress address;

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
