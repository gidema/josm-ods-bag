package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.EntityStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagPdokLigplaatsParser extends BagFeatureParser {
    private final EntityStore<OdBuilding> buildingStore;

    public BagPdokLigplaatsParser(CRSUtil crsUtil, EntityStore<OdBuilding> buildingStore) {
        super(crsUtil);
        this.buildingStore = buildingStore;
    }

    public void parse(SimpleFeature feature, DownloadResponse response) {
        BagOdBuilding building = new BagOdBuilding();
        super.parse(feature, building, response);
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        building.setStartYear(bouwjaar);
        building.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        building.setBuildingId(FeatureUtil.getLong(feature, "identificatie"));
        OdAddress address = parseAddress(feature);
        building.setAddress(address);
        building.setBuildingType(BuildingType.HOUSEBOAT);
        buildingStore.add(building);
    }

    private static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Plaats aangewezen":
            return EntityStatus.IN_USE;
        default:
            return EntityStatus.UNKNOWN;
        }
    }
}
