package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.BuildingType;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGtBuildingBuilder extends BagGtEntityBuilder<Building, BagBuilding> {
    
    public BagGtBuildingBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    protected BagBuilding newInstance() {
        return new BagBuilding();
    }

    @Override
    public BagBuilding build(SimpleFeature feature, DownloadResponse response) {
        BagBuilding building = super.build(feature, response);
        String type = feature.getName().getLocalPart();
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        if (bouwjaar != null) {
            building.setStartDate(bouwjaar.toString());
        }
        // Find a better way to handle the mapping from feature attributes to Entity attributes
        String status = FeatureUtil.getString(feature, "status");
        if (status == null) {
            status = FeatureUtil.getString(feature, "pandstatus");
        }
        building.setStatus(parseStatus(status));
        if (type.equals("bag:pand") || type.equals("osm_bag:buildingdestroyed_osm")) {
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

    private static EntityStatus parseStatus(String status) {
        if (status == null) {
            return EntityStatus.UNKNOWN;
        }
        switch (status) {
        case "Bouwvergunning verleend":
            return EntityStatus.PLANNED;
        case "Bouw gestart":
            return EntityStatus.CONSTRUCTION;
        case "Pand in gebruik":
        case "Pand in gebruik (niet ingemeten)":
        case "Pand buiten gebruik":
        case "Plaats aangewezen":
            return EntityStatus.IN_USE;
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
