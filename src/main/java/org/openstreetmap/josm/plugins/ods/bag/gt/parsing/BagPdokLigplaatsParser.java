package org.openstreetmap.josm.plugins.ods.bag.gt.parsing;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdLigplaats;
import org.openstreetmap.josm.plugins.ods.bag.entity.StatusPlaats;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class BagPdokLigplaatsParser extends BagFeatureParser {
    private final OdEntityStore<BagOdLigplaats, Long> ligplaatsStore;

    public BagPdokLigplaatsParser(CRSUtil crsUtil, OdEntityStore<BagOdLigplaats, Long> ligplaatsStore) {
        super(crsUtil);
        this.ligplaatsStore = ligplaatsStore;
    }

    @Override
    public void parse(SimpleFeature feature, DownloadResponse response) {
        BagOdLigplaats ligplaats = new BagOdLigplaats();
        super.parse(feature, ligplaats, response);
        ligplaats.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        ligplaats.setId(FeatureUtil.getLong(feature, "identificatie"));
        OdAddress address = parseAddress(feature);
        ligplaats.setAddress(address);
        ligplaatsStore.add(ligplaats);
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
