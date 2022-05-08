package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;

public interface OsmStreet extends OsmEntity {
    public OsmCity getCity();

    public String getName();

    public String getCityName();

    public String getStreetName();
}
