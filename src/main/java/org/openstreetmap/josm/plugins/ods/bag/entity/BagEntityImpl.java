package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.AbstractEntity;

public abstract class BagEntityImpl extends AbstractEntity {
    private boolean incomplete;
    private Long identificatie;

//    public void setInternal(boolean internal) {
//        this.internal = internal;
//    }
    
    public void setIdentificatie(Long identificatie) {
        this.identificatie = identificatie;    
    }
    
//    @Override
//    public boolean isInternal() {
//        return internal;
//    }

    @Override
    public boolean isIncomplete() {
        return incomplete;
    }


//    @Override
//    public boolean hasReferenceId() {
//        return hasReferenceId;
//    }

    @Override
    public Long getReferenceId() {
        return identificatie;
    }
}
