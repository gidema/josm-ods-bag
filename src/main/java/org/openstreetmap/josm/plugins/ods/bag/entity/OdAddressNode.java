package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.locationtech.jts.geom.Point;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface OdAddressNode extends OdEntity {
    public Long getAddressId();
    
    @Override
    public Point getGeometry();

    public NLAddress getAddress();

    public Object getBuildingRef();

    public BagAddressableObject getAddressableObject();

    public void setAddressableObject(BagAddressableObject object);
    
    public AddressableObjectStatus getStatus();
}
