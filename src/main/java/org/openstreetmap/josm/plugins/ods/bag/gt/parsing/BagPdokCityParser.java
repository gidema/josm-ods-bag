package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.places.OdCity;
import org.openstreetmap.josm.plugins.ods.domains.places.impl.AbstractOdCity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagPdokCityParser extends BagFeatureParser {
    private final OdEntityStore<OdCity, Long> cityStore;

    public BagPdokCityParser(CRSUtil crsUtil, OdEntityStore<OdCity, Long> cityStore) {
        super(crsUtil);
        this.cityStore = cityStore;
    }

    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        AbstractOdCity city = new AbstractOdCity();
        super.parse(feature, city, response);
        city.setName(FeatureUtil.getString(feature, "woonplaats"));
        cityStore.add(city);
    }
}
