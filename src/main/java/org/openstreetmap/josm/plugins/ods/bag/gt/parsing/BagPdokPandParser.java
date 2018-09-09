package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingStatus;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.tools.Logging;

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

    private static BuildingStatus parseStatus(String status) {
        switch (status) {
        case "Bouwvergunning verleend":
            return BuildingStatus.PROJECTED;
        case "Bouw gestart":
            return BuildingStatus.UNDER_CONSTRUCTION;
        case "Pand in gebruik":
            return BuildingStatus.FUNCTIONAL;
        case "Pand in gebruik (niet ingemeten)":
            return BuildingStatus.IN_USE_NOT_MEASURED;
        case "Niet gerealiseerd pand":
            return BuildingStatus.NOT_ESTABLISHED;
        case "Sloopvergunning verleend":
            return BuildingStatus.DEMOLITION_DUE;
        case "Pand gesloopt":
            return BuildingStatus.DEMOLISHED;
        case "Pand buiten gebruik":
            return BuildingStatus.DECLINED;
        case "Verbouwing pand":
            return BuildingStatus.UNDER_RECONSTRUCTION;
        case "Pand ten onrechte opgevoerd":
            return BuildingStatus.WITHDRAWN;
        default:
            Logging.warn("Unknown Pand status: {0}", status);
            return BuildingStatus.UNKNOWN;
        }
    }
}
