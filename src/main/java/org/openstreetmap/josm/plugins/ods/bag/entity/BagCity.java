package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.actual.City;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class BagCity extends BagEntityImpl implements City {
    private String name;
    private MultiPolygon multiPolygon;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
    public boolean isDeleted() {
        return false;
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }
}
