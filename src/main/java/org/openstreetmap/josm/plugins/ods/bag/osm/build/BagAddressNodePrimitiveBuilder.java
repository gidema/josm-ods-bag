package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.osm.build.AddressPrimitiveBuilder;

public class BagAddressNodePrimitiveBuilder extends BagPrimitiveBuilder<AddressNode> {

    public BagAddressNodePrimitiveBuilder(DataSet targetDataSet) {
        super(targetDataSet);
    }

    @Override
    protected void buildTags(AddressNode addresNode, OsmPrimitive primitive) {
        AddressPrimitiveBuilder.buildTags(addresNode.getAddress(), primitive);
        primitive.put("source", "BAG");
        primitive.put("source:date", addresNode.getSourceDate());
    }
}
