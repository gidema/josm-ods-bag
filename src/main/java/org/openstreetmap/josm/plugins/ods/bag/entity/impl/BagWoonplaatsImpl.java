package org.openstreetmap.josm.plugins.ods.bag.entity.impl;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagCityStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagWoonplaats;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class BagWoonplaatsImpl extends AbstractOdEntity implements BagWoonplaats {
    private Long woonplaatsId;
    private String name;
    private BagCityStatus status;
    private MultiPolygon multiPolygon;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getWoonplaatsId() {
        return woonplaatsId;
    }

    public void setWoonplaatsId(Long woonplaatsId) {
        this.woonplaatsId = woonplaatsId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStatusTag() {
        return getStatus().toString();
    }

    @Override
    public BagCityStatus getStatus() {
        return status;
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
    public Mapping<? extends OsmEntity, ? extends OdEntity> getMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean readyForImport() {
        // This plug-in should not be used to import place boundaries 
        return false;
    }

    @Override
    public UpdateTaskType getUpdateTaskType() {
        return UpdateTaskType.NONE;
    }
}
