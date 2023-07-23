package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingUnitStatus;
import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class OsmNlAddressNode extends AbstractOsmEntity implements OsmAddressNode {
    private Long addressableId;
    private OsmAddress address;
    private OsmBuilding building;
    private BuildingUnitStatus status;
    private AddressNodeMatch match;

    public OsmNlAddressNode() {
        super();
    }

    @Override
    public Long getPrimaryId() {
        return getAddressableId();
    }

    @Override
    public Long getAddressableId() {
        return addressableId;
    }

    @Override
    public void setAddressableId(Long addressableId) {
        this.addressableId = addressableId;
    }

    @Override
    public void setAddress(OsmAddress address) {
        this.address = address;
    }

    @Override
    public OsmAddress getAddress() {
        return address;
    }

    @Override
    public void setBuilding(OsmBuilding building) {
        this.building = building;
    }

    @Override
    public OsmBuilding getBuilding() {
        return building;
    }

    @Override
    public void setGeometry(Point point) {
        super.setGeometry(point);
    }

    @Override
    public Point getGeometry() {
        try {
            return (Point) super.getGeometry();
        }
        catch (ClassCastException e) {
            Logging.warn(I18n.tr("The geometry of {0} is not a point", toString()));
            return super.getGeometry().getCentroid();
        }
    }   
    
    @Override
    public AddressNodeMatch getMatch() {
        return match;
    }

    @Override
    public void setMatch(AddressNodeMatch match) {
        this.match = match;
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }

    @Override
    public BuildingUnitStatus getStatus() {
        return status;
    }
}
