package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

import com.vividsolutions.jts.geom.Geometry;

public class BagOsmBuildingBuilder extends AbstractOsmEntityBuilder<Building> {

    public BagOsmBuildingBuilder(OdsModule module) {
        super(module, BuildingEntityType.getInstance());
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (getEntityType().recognize(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeTags(primitive);
                BagBuilding building = new BagBuilding();
                Map<String, String> tags = primitive.getKeys();
                parseKeys(building, tags);
                building.setOtherTags(tags);
                try {
                    Geometry geometry = buildGeometry(primitive);
                    building.setGeometry(geometry);
                } catch (InvalidGeometryException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                register(primitive, building);
            }
        }
        return;
    }
    
    public static void normalizeTags(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
        String bouwjaar = primitive.get("bag:bouwjaar");
        if (bouwjaar != null) {
            primitive.put("start_date", bouwjaar);
            primitive.remove("bag:bouwjaar");
        }
    }
    
    private static void parseKeys(BuildingImpl building, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(building, tags);
        String type = tags.remove("building");
        if (type == null) {
            type = tags.remove("building:part");
        }
        if (type.equals("construction")) {
            building.setUnderConstruction(true);
            String construction = tags.remove("construction");
            if (construction != null) {
                 type = construction;
            }
        }
        building.setBuildingType(getBuildingType(type, tags));
        if (tags.remove("3dshapes:ggmodelk") != null) {
            building.setSource("3dshapes");
        }
        building.setStartDate(tags.remove("start_date"));
        if (tags.containsKey("addr:housenumber")) {
            BagAddress address = new BagAddress();
            BagOsmAddressEntityBuilder.parseKeys(address, tags);
            building.setAddress(address);
        }
        return;
    }
    
    private static BuildingType getBuildingType(String type, Map<String, String> tags) {
        switch(type) {
        case "house":
            return BuildingType.HOUSE;
        case "houseboat":
            tags.remove("floating");
            return BuildingType.HOUSEBOAT;
        case "static_caravan":
            return BuildingType.STATIC_CARAVAN;
        case "apartments":
            return BuildingType.APARTMENTS;
        case "industrial":
            return BuildingType.INDUSTRIAL ;
        case "retail":
            return BuildingType.RETAIL;
        case "office":
            return BuildingType.OFFICE;
        case "garage":
            return BuildingType.GARAGE;
        case "yes":
            if ("substation".equals(tags.get("power"))) {
                return BuildingType.SUBSTATION;
            }
            return BuildingType.UNCLASSIFIED;
        default:
            return BuildingType.OTHER(type);
        }
    }
    
    private Geometry buildGeometry(OsmPrimitive primitive) throws InvalidGeometryException {
        if (primitive.getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
            return buildGeometry((Way)primitive);
        }
        else if (primitive.getType() == OsmPrimitiveType.RELATION) {
            return buildGeometry((Relation)primitive);
        }
        return null;
    }

    private Geometry buildGeometry(Way way) throws IllegalArgumentException {
        return getGeoUtil().toPolygon(way);
    }
    
    private Geometry buildGeometry(Relation relation) throws InvalidMultiPolygonException {
        return getGeoUtil().toMultiPolygon(relation);
    }
}
