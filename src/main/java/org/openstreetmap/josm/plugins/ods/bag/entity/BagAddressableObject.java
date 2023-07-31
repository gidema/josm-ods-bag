package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface BagAddressableObject extends OdEntity {
    public NLAddress getMainAddress();
    public AddressableObjectStatus getAddressableStatus();
    public Long getId();
}
