package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagWoonplaats;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class BagCity extends BagWoonplaatsImpl {
    private String name;
    private MultiPolygon multiPolygon;
    private long cityId;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setIdentificatie(long cityId) {
        this.cityId = cityId;
    }

    public long getCityId() {
        return cityId;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        switch (geometry.getGeometryType()) {
        case "MultiPolygon":
            multiPolygon = (MultiPolygon) geometry;
            break;
        case "Polygon":
            multiPolygon = geometry.getFactory().createMultiPolygon(
                    new Polygon[] {(Polygon) geometry});
            break;
        default:
            // TODO intercept this exception or accept null?
        }
    }

    @Override
    public MultiPolygon getGeometry() {
        return multiPolygon;
    }

    @Override
    public Mapping<OsmCity, BagWoonplaats> getMapping() {
        return null;
    }
}
