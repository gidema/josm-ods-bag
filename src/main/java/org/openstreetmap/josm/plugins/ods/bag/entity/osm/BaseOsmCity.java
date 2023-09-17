package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagCity;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class BaseOsmCity extends AbstractOsmEntity implements OsmCity {
    private Long cityId;
    private String name;
    private MultiPolygon multiPolygon;

    public void setName(String name) {
        this.name = name;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
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
    public Mapping<OsmCity, BagCity> getMapping() {
        return null;
    }
}
