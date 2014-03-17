package org.openstreetmap.josm.plugins.ods.bag.osm;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import com.vividsolutions.jts.geom.Geometry;

public class BagBuildingBuilder extends BagEntityBuilder<BagBuilding> {
    private BagAddressBuilder addressBuilder = new BagAddressBuilder();
    private final static GeoUtil geoUtil = GeoUtil.getInstance();
    
    @Override
    public BagBuilding build(OsmPrimitive primitive, MetaData metaData) {
        BagBuilding building = super.build(primitive, metaData);
        if (addressBuilder.active()) {
            building.setAddress(addressBuilder.getAddress());
        }
        return building;
    }

    @Override
    protected BagBuilding createEntity() {
        addressBuilder.reset();
        return new BagBuilding();
    }

    @Override
    protected Map<String, String> parseKeys(BagBuilding entity,
            OsmPrimitive primitive) {
        return super.parseKeys(entity, primitive);
    }

    @Override
    protected boolean parseKey(BagBuilding building, String key, String value) {
        if (super.parseKey(building, key, value)) {
            return true;
        }
        if (addressBuilder.parseKey(key, value)) {
            return true;
        }
        if ("building".equals(key)) {
            if ("construction".equals(value)) {
                building.setUnderConstruction(true);
            } else {
                building.setBuildingType(value);
            }
            return true;
        }
        if ("construction".equals(key)) {
            if (building.isUnderConstruction()) {
                building.setBuildingType(value);
                return true;
            }
            return false;
        }
        if ("3dshapes:ggmodelk".equals(key)
                || ("source".equals(key) && "3dShapes".equalsIgnoreCase(value))) {
            building.setSource("3dshapes");
            return true;
        }
        if ("start_date".equals(key)) {
            building.setStartDate(value);
            return true;
        }
        if ("bag:bouwjaar".equals(key)) {
            building.setStartDate(value);
            return true;
        }
        return false;
    }

    @Override
    protected Geometry buildGeometry(OsmPrimitive primitive)
            throws InvalidGeometryException {
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
            return geoUtil.toPolygon((Way)primitive);
        }
        else if (primitive.getDisplayType() == OsmPrimitiveType.MULTIPOLYGON) {
            return geoUtil.toMultiPolygon((Relation)primitive);
        }
        return null;
    }
}
