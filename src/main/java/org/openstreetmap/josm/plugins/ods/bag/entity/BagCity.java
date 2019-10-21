package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.domains.places.impl.AbstractOdCity;
import org.openstreetmap.josm.plugins.ods.matching.Match;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class BagCity extends AbstractOdCity {
    private String name;
    private MultiPolygon multiPolygon;
    private long identificatie;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setIdentificatie(long identificatie) {
        this.identificatie = identificatie;
    }

    public long getIdentificatie() {
        return identificatie;
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
    public Match<OsmCity, OdCity> getMatch() {
        return null;
    }
}
