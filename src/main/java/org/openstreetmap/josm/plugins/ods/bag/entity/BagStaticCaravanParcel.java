package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagStaticCaravanParcel extends OdEntity, NlAddressable {
    public Long getStandplaatsId();

    public ParcelStatus getStatus();
    
    public void setMatch(OdMatch<BagStaticCaravanParcel> match);
}
