package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGtBuildingBuilder extends BagGtEntityBuilder<OdBuilding> {

    public BagGtBuildingBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    public OdBuilding build(SimpleFeature feature, DownloadResponse response) {
        BagOdBuilding building = new BagOdBuilding();
        super.parse(feature, building, response);
        String type = feature.getName().getLocalPart();
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        building.setStartYear(bouwjaar);
        building.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        building.setBuildingId(FeatureUtil.getLong(feature, "identificatie"));
        if (type.equals("bag:pand")) {
            building.setBuildingType(BuildingType.UNCLASSIFIED);
            building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        }
        else {
            BagOdAddress address = new BagOdAddress();
            address.setHouseNumber(FeatureUtil.getInteger(feature, "huisnummer"));
            address.setHuisletter(FeatureUtil.getString(feature, "huisletter"));
            address.setHuisnummerToevoeging(FeatureUtil.getString(feature, "toevoeging"));
            address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
            address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
            address.setPostcode(FeatureUtil.getString(feature, "postcode"));
            building.setAddress(address);
            if (type.equals("bag:ligplaats")) {
                building.setBuildingType(BuildingType.HOUSEBOAT);
            }
            else if (type.equals("bag:standplaats")) {
                building.setBuildingType(BuildingType.STATIC_CARAVAN);
            }
            else {
                building.setBuildingType(BuildingType.UNCLASSIFIED);
            }
        }
        return building;
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
