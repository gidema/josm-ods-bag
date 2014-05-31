package org.openstreetmap.josm.plugins.ods.bag.gt;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.BagAddress;
import org.openstreetmap.josm.plugins.ods.bag.BagBuilding;
import org.openstreetmap.josm.plugins.ods.crs.CRSException;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.geotools.FeatureUtil;

public class BagBuildingBuilder extends BagEntityBuilder<BagBuilding> {
    private String type;
    
    public BagBuildingBuilder(String type) {
        super();
        this.type = type;
    }

    @Override
    protected BagBuilding createEntity() {
        BagBuilding building = new BagBuilding();
        building.setInternal(false);
        building.setBuildingType(type);
        return building;
    }

    @Override
    protected void parseData(BagBuilding building, SimpleFeature feature) {
        super.parseData(building, feature);
        building.setBouwjaar(FeatureUtil.getInteger(feature, "bouwjaar"));
        building.setStatus(FeatureUtil.getString(feature, "status"));
        if ("bouw gestart".equalsIgnoreCase(building.getStatus())) {
            building.setUnderConstruction(true);
        }
        building.setOppervlakteMin(FeatureUtil.getDouble(feature, "oppervlakte_min"));
        building.setOppervlakteMax(FeatureUtil.getDouble(feature, "oppervlakte_max"));
        building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        try {
            building.setGeometry(CRSUtil.getInstance().transform(feature));
        } catch (CRSException e) {
            throw new RuntimeException(e);
        }
        
        switch (type) {
        case "houseboat":
        case "static_caravan":
            BagAddress address = BagAddressBuilder.build(feature);
            building.setAddress(address);
            return;
        default:
            return;
        }
    }   
}
