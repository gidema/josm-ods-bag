package org.openstreetmap.josm.plugins.ods.bag.gt.build;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.places.impl.AbstractOdCity;
import org.openstreetmap.josm.plugins.ods.domains.places.impl.OpenDataCityStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagGtCityBuilder extends BagGtEntityBuilder<AbstractOdCity> {
    public OpenDataCityStore cityStore;

    public BagGtCityBuilder(CRSUtil crsUtil) {
        super(crsUtil);
    }

    @Override
    public AbstractOdCity build(SimpleFeature feature, DownloadResponse response) {
        AbstractOdCity city = new AbstractOdCity();
        super.parse(feature, city, response);
        city.setName(FeatureUtil.getString(feature, "woonplaats"));
        return city;
    }
}
