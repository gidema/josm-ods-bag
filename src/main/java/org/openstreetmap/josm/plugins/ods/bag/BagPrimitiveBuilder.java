package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagDemolishedBuildingPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagMooringParcelPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagStaticCaravanParcelPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public class BagPrimitiveBuilder implements OdsContextJob {

    public BagPrimitiveBuilder() {
    }

    // TODO Move this to the module configuration
    public void run(OdsContext context) {
        new BagBuildingEntityPrimitiveBuilder().run(context);
        new BagMooringParcelPrimitiveBuilder().run(context);
        new BagStaticCaravanParcelPrimitiveBuilder().run(context);
        new BagAddressNodeEntityPrimitiveBuilder().run(context);
        new BagDemolishedBuildingPrimitiveBuilder().run(context);
    }
}
