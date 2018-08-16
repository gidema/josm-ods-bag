package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagPdokPandParser extends BagFeatureParser {
    private final OdEntityStore<OdBuilding, Long> buildingStore;
    //    private final SimpleFeatureType featureType;

    public  BagPdokPandParser(CRSUtil crsUtil,
            //            SimpleFeatureType featureType,
            OdEntityStore<OdBuilding, Long> buildingStore) {
        super(crsUtil);
        //        this.featureType = featureType;
        this.buildingStore = buildingStore;
    }

    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        BagOdBuilding building = new BagOdBuilding();
        super.parse(feature, building, response);
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        building.setStartYear(bouwjaar);
        building.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        building.setBuildingId(FeatureUtil.getLong(feature, "identificatie"));
        building.setBuildingType(BuildingType.UNCLASSIFIED);
        building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        buildingStore.add(building);
    }

    private static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Bouwvergunning verleend":
            return EntityStatus.PLANNED;
        case "Bouw gestart":
            return EntityStatus.CONSTRUCTION;
        case "Pand in gebruik":
        case "Pand buiten gebruik":
        case "Plaats aangewezen":
            return EntityStatus.IN_USE;
        case "Pand in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE_NOT_MEASURED;
        case "Niet gerealiseerd pand":
            return EntityStatus.NOT_REALIZED;
        case "Sloopvergunning verleend":
            return EntityStatus.REMOVAL_DUE;
        case "Pand gesloopt":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.UNKNOWN;
        }
    }
}
