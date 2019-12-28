package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagAddressNodeEntityPrimitiveBuilder extends BagEntityPrimitiveBuilder<OdAddressNode> {

    public BagAddressNodeEntityPrimitiveBuilder(OdLayerManager dataLayer) {
        super(dataLayer);
    }

    @Override
    protected void buildTags(OdAddressNode addressNode, Map<String, String> tags) {
        AddressConversionAlgorithms.bagToOsm(addressNode.getAddress(), tags::put);
        tags.put("source", "BAG");
        tags.put("source:date", addressNode.getSourceDate());
    }
}
