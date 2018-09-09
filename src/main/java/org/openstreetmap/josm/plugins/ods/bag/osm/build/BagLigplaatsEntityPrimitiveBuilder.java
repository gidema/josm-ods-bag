package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdLigplaats;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;

public class BagLigplaatsEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagOdLigplaats> {

    public BagLigplaatsEntityPrimitiveBuilder(OdLayerManager dataLayer, OdEntityStore<BagOdLigplaats, Long> ligplaatsStore) {
        super(dataLayer, ligplaatsStore);
    }

    @Override
    public void createPrimitive(BagOdLigplaats ligplaats) {
        super.createPrimitive(ligplaats);
    }

    @Override
    protected void buildTags(BagOdLigplaats ligplaats, Map<String, String> tags) {
        OdAddress address = ligplaats.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", ligplaats.getSourceDate());
        tags.put("ref:bag", ligplaats.getId().toString());
        tags.put("building", "houseboat");
        tags.put("floating", "yes");
    }
}
