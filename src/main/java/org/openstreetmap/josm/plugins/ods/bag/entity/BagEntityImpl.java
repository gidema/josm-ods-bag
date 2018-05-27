package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;

public abstract class BagEntityImpl extends AbstractOdEntity {
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

    //    @Override
    //    public boolean hasReferenceId() {
    //        return hasReferenceId;
    //    }

    @Override
    public Long getReferenceId() {
        return identificatie;
    }
}
