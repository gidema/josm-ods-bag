package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OdAddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        BagAddressNodeStore addressNodeStore =context.getComponent(BagAddressNodeStore.class);
        addressNodeStore.stream()
        .filter(addressNode->addressNode.getPrimitive() == null)
        .forEach(entity -> super.createPrimitive(entity, layerManager));
    }


    @Override
    protected void buildTags(OdAddressNode addresNode, Map<String, String> tags) {
        createAddressTags(addresNode.getAddress(), tags);
        tags.put("source", "BAG");
        tags.put("source:date", addresNode.getSourceDate());
    }
}
