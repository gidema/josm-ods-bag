package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdStandplaats;
import org.openstreetmap.josm.plugins.ods.bag.entity.StatusPlaats;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagPdokStandplaatsParser extends BagFeatureParser {
    private final OdEntityStore<BagOdStandplaats, Long> standplaatsStore;

    public BagPdokStandplaatsParser(CRSUtil crsUtil, OdEntityStore<BagOdStandplaats, Long> standplaatsStore) {
        super(crsUtil);
        this.standplaatsStore = standplaatsStore;
    }

    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        BagOdStandplaats standplaats = new BagOdStandplaats();
        super.parse(feature, standplaats, response);
        standplaats.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        standplaats.setId(FeatureUtil.getLong(feature, "identificatie"));
        OdAddress address = parseAddress(feature);
        standplaats.setAddress(address);
        standplaatsStore.add(standplaats);
    }

    private static StatusPlaats parseStatus(String status) {
        switch (status) {
        case "Plaats aangewezen":
            return StatusPlaats.AANGEWEZEN;
        case "Plaats ingetrokken":
            return StatusPlaats.INGETROKKEN;
        default:
            return StatusPlaats.ONBEKEND;
        }
    }
}
