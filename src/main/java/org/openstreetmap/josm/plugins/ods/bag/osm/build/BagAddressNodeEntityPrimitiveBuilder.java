package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OdAddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder(OdLayerManager dataLayer, OdEntityStore<OdAddressNode, ?> entityStore) {
        super(dataLayer, entityStore);
    }

    @Override
    protected void buildTags(OdAddressNode addresNode, Map<String, String> tags) {
        createAddressTags(addresNode.getAddress(), tags);
        tags.put("source", "BAG");
        tags.put("source:date", addresNode.getSourceDate());
    }
}
