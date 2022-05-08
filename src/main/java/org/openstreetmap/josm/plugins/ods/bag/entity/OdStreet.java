package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;

public interface OdStreet extends OdEntity {
    public BagWoonplaats getCity();

    public String getName();

    public String getCityName();

    public String getStreetName();
}
