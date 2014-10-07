package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CityImpl;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GtCityStore;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;

public class BagGtCityBuilder extends BagGtEntityBuilder<CityImpl> {
    public GtCityStore cityStore;
    
    public BagGtCityBuilder(CRSUtil crsUtil, GtCityStore cityStore) {
        super(crsUtil);
        this.cityStore = cityStore;
    }

    @Override
    public void buildGtEntity(SimpleFeature feature) {
        CityImpl city = new CityImpl();
        super.build(city, feature);
        city.setName(FeatureUtil.getString(feature, "woonplaats"));
        cityStore.add(city);
    }
}
