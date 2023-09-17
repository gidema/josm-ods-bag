package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingUnitStatus;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface OsmAddressNode extends OsmEntity {
    default Long getPrimaryId() {
        return getAddressableId();
    }

    public Long getAddressableId();
    
    public OsmAddress getAddress();

    public void setAddress(OsmAddress address);

    public void setBuilding(OsmBuilding building);

    public OsmBuilding getBuilding();

    public void setGeometry(Point point);

    @Override
    public Point getGeometry();

    public void setAddressableId(Long id);

    public BuildingUnitStatus getStatus();
}
