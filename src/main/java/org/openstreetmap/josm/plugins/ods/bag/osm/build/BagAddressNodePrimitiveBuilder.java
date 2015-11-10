package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.LayerManager;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;

public class BagAddressNodePrimitiveBuilder extends BagPrimitiveBuilder<AddressNode> {

    public BagAddressNodePrimitiveBuilder(LayerManager dataLayer) {
        super(dataLayer);
    }

    @Override
    protected void buildTags(AddressNode addresNode, Map<String, String> tags) {
        AddressPrimitiveBuilder.buildTags(addresNode.getAddress(), tags);
        tags.put("source", "BAG");
        tags.put("source:date", addresNode.getSourceDate());
    }
}
