package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanParcel;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanParcelStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagStaticCaravanParcelPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagStaticCaravanParcel> {

    public BagStaticCaravanParcelPrimitiveBuilder() {
        super();
    }

    @Override
    public void run(OdsContext context) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        BagStaticCaravanParcelStore standplaatsStore = context.getComponent(BagStaticCaravanParcelStore.class);
        standplaatsStore.stream()
        .filter(standplaats->standplaats.getPrimitive() == null)
        .forEach(entity -> super.createPrimitive(entity, layerManager));
    }

    @Override
    protected void buildTags(BagStaticCaravanParcel standplaats, Map<String, String> tags) {
        NLAddress address = standplaats.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", standplaats.getSourceDate());
        tags.put("ref:bag", standplaats.getStandplaatsId().toString());
        // TODO Some municipalities draw the outline of the static caravan itself, whereas other draw the
        // outline of the parcel. There tends to be a shift to the latter, which is more conform the BAG standards.
        // In the beginning, we imported 'Standplaats' objects as building=static_caravan
        // Now we might consider to switch towards tagging that maps the outline of the parcel.
        // Some mappers tag the 'parcel' as 'landuse = static_caravan'. Personally I would prefer
        // 'parcel = static_caravan_site' or something like that
        // As the parcels tend to be a lot bigger than the caravans, we might consider the area of the geometry to
        // differentiate between them. 
        tags.put("building", "static_caravan");
    }
}
