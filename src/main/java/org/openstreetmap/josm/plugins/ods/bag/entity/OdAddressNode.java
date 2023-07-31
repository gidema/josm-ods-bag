package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.locationtech.jts.geom.Point;

public interface OdAddressNode extends OdEntity {
    public Long getAddressId();
    
    @Override
    public Point getGeometry();

    @Override
    public AddressNodeMatch getMatch();

    public NLAddress getAddress();

    public Object getBuildingRef();

    public BagAddressableObject getAddressableObject();

    public void setAddressableObject(BagAddressableObject object);
    
    public AddressableObjectStatus getStatus();
    
    public void setMatch(AddressNodeMatch match);
}
