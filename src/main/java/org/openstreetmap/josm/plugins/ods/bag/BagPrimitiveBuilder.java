package org.openstreetmap.josm.plugins.ods.bag;

import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.context.OdsContextJob;

public class BagPrimitiveBuilder implements OdsContextJob {
    private List<BagEntityPrimitiveBuilder<?>> primitiveBuilders;

    public BagPrimitiveBuilder(
            List<BagEntityPrimitiveBuilder<?>> primitiveBuilders) {
        super();
        this.primitiveBuilders = primitiveBuilders;
    }

    @Override
    public void run(OdsContext context) {
        primitiveBuilders.forEach(builder -> builder.run(context));
    }
}
