package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;

public class BagOsmBuildingBuilder extends AbstractOsmEntityBuilder<OsmBuilding> {

    private final OdsContext context;

    public BagOsmBuildingBuilder(OdsContext context) {
        super(context, OsmBuilding.class);
        this.context = context;
    }

    private static boolean canHandle(OsmPrimitive primitive) {
        return OsmBuilding.isBuilding(primitive);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!getEntityStore().contains(primitive.getId())) {
                normalizeTags(primitive);
                OsmBagBuilding building = new OsmBagBuilding();
                Map<String, String> tags = primitive.getKeys();
                parseKeys(building, tags);
                building.setOtherTags(tags);

                Geometry geometry = buildGeometry(primitive);
                building.setGeometry(geometry);
                register(primitive, building);
            }
        }
        return;
    }

    public static void normalizeTags(OsmPrimitive primitive) {
        BagOsmEntityBuilder.normalizeTags(primitive);
    }

    private static void parseKeys(OsmBagBuilding building, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(building, tags);
        String bagId = tags.remove("ref:bag");
        building.setBuildingId(BagOsmEntityBuilder.getReferenceId(bagId));
        String type = tags.remove("building");
        if (type == null) {
            type = tags.remove("building:part");
        }
        if (type.equals("construction")) {
            building.setStatus(BuildingStatus.CONSTRUCTION);
            String construction = tags.remove("construction");
            type = (construction == null ? "yes" : construction);
        }
        else {
            building.setStatus(BuildingStatus.IN_USE);
        }
        building.setBuildingType(getBuildingType(type, tags));
        building.setStartDate(tags.remove("start_date"));
        if (tags.containsKey("addr:housenumber")) {
            BagOsmAddress address = new BagOsmAddress();
            BagOsmAddressEntityBuilder.parseKeys(address, tags);
            building.setMainAddress(address);
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
            return BuildingType.OTHER;
        }
    }

    private Geometry buildGeometry(OsmPrimitive primitive) {
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

    private Geometry buildGeometry(Relation relation) {
        return getGeoUtil().toMultiPolygon(relation);
    }

    @Override
    public OsmBuildingStore getEntityStore() {
        return context.getComponent(OsmBuildingStore.class);
    }
}
