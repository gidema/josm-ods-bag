package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.actual.City;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.CityImpl;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataCityStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGtCityBuilder extends BagGtEntityBuilder<City, CityImpl> {
    public OpenDataCityStore cityStore;
    
    public BagGtCityBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    protected CityImpl newInstance() {
        return new CityImpl();
    }

    @Override
    public CityImpl build(SimpleFeature feature, DownloadResponse response) {
        CityImpl city = super.build(feature, response);
        city.setName(FeatureUtil.getString(feature, "woonplaats"));
        return city;
    }
}
