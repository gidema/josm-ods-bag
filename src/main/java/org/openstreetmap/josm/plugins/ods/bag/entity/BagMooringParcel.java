package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagMooringParcel extends OdEntity, NlAddressable {

    public Long getLigplaatsId();
    
    public ParcelStatus getStatus();
    
    public void setMatch(OdMatch<BagMooringParcel> match);
}
