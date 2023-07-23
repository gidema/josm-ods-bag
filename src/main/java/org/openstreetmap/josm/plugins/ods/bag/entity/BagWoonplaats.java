package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

import org.locationtech.jts.geom.MultiPolygon;

public interface BagWoonplaats extends OdEntity {

    String TYPE = "ods:city";

    public Long getWoonplaatsId();

    public String getName();
    
    public BagCityStatus getStatus();

    @Override
    public MultiPolygon getGeometry();
}
