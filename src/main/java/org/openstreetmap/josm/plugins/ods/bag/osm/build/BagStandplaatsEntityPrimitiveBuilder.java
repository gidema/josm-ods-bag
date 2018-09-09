package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdStandplaats;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;

public class BagStandplaatsEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagOdStandplaats> {

    public BagStandplaatsEntityPrimitiveBuilder(OdLayerManager dataLayer, OdEntityStore<BagOdStandplaats, Long> standplaatsStore) {
        super(dataLayer, standplaatsStore);
    }

    @Override
    public void createPrimitive(BagOdStandplaats standplaats) {
        super.createPrimitive(standplaats);
    }

    @Override
    protected void buildTags(BagOdStandplaats standplaats, Map<String, String> tags) {
        OdAddress address = standplaats.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", standplaats.getSourceDate());
        tags.put("ref:bag", standplaats.getId().toString());
        tags.put("building", "static_caravan");
    }
}
