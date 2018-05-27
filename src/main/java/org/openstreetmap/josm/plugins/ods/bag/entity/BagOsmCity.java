package org.openstreetmap.josm.plugins.ods.bag.entity;

import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Complete;
import static org.openstreetmap.josm.plugins.ods.entities.Entity.Completeness.Incomplete;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.ods.domains.places.impl.BaseOsmCity;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class BagOsmCity extends BaseOsmCity {
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
    public Completeness getCompleteness() {
        OsmPrimitive osm = getPrimitive();
        switch (osm.getDisplayType()) {
        case CLOSEDWAY:
            return Complete;
        case MULTIPOLYGON:
            if (((Relation)osm).hasIncompleteMembers()) {
                return Incomplete;
            }
            return Complete;
        default:
            return Incomplete;
        }
    }
}
