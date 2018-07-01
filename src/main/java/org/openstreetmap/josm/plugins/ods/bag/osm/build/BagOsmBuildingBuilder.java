package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOsmAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.BaseOsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.osm.AbstractOsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;

public class BagOsmBuildingBuilder extends AbstractOsmEntityBuilder<OsmBuilding> {

    private final OsmBuildingStore buildingStore;

    public BagOsmBuildingBuilder(OsmLayerManager layerManager,
            OsmBuildingStore buildingStore, GeoUtil geoUtil) {
        super(layerManager, buildingStore, geoUtil);
        this.buildingStore = buildingStore;
    }

    @Override
    public boolean canHandle(OsmPrimitive primitive) {
        return OsmBuilding.IsBuilding(primitive);
    }

    @Override
    public void buildOsmEntity(OsmPrimitive primitive) {
        if (canHandle(primitive)) {
            if (!buildingStore.contains(primitive.getId())) {
                normalizeTags(primitive);
                BaseOsmBuilding building = new BaseOsmBuilding();
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
        String bouwjaar = primitive.get("bag:bouwjaar");
        if (bouwjaar != null) {
            primitive.put("start_date", bouwjaar);
            primitive.remove("bag:bouwjaar");
        }
    }

    private static void parseKeys(BaseOsmBuilding building, Map<String, String> tags) {
        BagOsmEntityBuilder.parseKeys(building, tags);
        String sId = tags.get("ref:bag");
        if (sId != null) {
            try {
                building.setBuildingId(Long.valueOf(sId));
                tags.remove("ref:bag");
            }
            catch (NumberFormatException e) {
                //TODO Create Validation TestError
            }
        }
        String type = tags.remove("building");
        if (type == null) {
            type = tags.remove("building:part");
        }
        if (type.equals("construction")) {
            building.setStatus(EntityStatus.CONSTRUCTION);
            String construction = tags.remove("construction");
            type = (construction == null ? "yes" : construction);
        }
        else {
            building.setStatus(EntityStatus.IN_USE);
        }
        building.setBuildingType(getBuildingType(type, tags));
        building.setStartDate(tags.remove("start_date"));
        if (tags.containsKey("addr:housenumber")) {
            BagOsmAddress address = new BagOsmAddress();
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
}
