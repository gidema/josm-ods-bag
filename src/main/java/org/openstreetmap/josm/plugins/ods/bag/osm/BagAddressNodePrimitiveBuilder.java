package org.openstreetmap.josm.plugins.ods.bag.osm;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagAddressNode;
import org.openstreetmap.josm.plugins.ods.osm.AddressPrimitiveBuilder;

public class BagAddressNodePrimitiveBuilder extends BagPrimitiveBuilder<BagAddressNode> {

    public BagAddressNodePrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    public void buildTags(BagAddressNode addressNode, OsmPrimitive primitive) {
        super.buildTags(addressNode, primitive);
        AddressPrimitiveBuilder.buildTags(addressNode.getAddress(), primitive);
    }
    
}
