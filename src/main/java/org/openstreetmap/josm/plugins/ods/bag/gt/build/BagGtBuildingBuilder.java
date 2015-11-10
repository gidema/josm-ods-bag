package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagGtBuildingBuilder extends BagGtEntityBuilder<Building, BagBuilding> {
    
    public BagGtBuildingBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    protected BagBuilding newInstance() {
        return new BagBuilding();
    }

    @Override
    public BagBuilding build(SimpleFeature feature, MetaData metaData, DownloadResponse response) {
        BagBuilding building = super.build(feature, metaData, response);
        String type = feature.getName().getLocalPart();
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        if (bouwjaar != null) {
            building.setStartDate(bouwjaar.toString());
        }
        building.setStatus(FeatureUtil.getString(feature, "status"));
        if ("Bouw gestart".equalsIgnoreCase(building.getStatus())) {
            building.setUnderConstruction(true);
        }
        else if ("Pand gesloopt".equalsIgnoreCase(building.getStatus())) {
            building.setDeleted(true);
        } 
        if (type.equals("bag:pand")) {
            building.setBuildingType(BuildingType.UNCLASSIFIED);
            building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        }
        else {
            BagAddress address = new BagAddress();
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
}
