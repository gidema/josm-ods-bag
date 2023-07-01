package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringParcel;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringParcelStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagMooringParcelPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagMooringParcel> {

    public BagMooringParcelPrimitiveBuilder() {
        super();
    }

    
    @Override
    public void run(OdsContext context) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        BagMooringParcelStore ligplaatsStore = context.getComponent(BagMooringParcelStore.class);
        ligplaatsStore.stream()
        .filter(ligplaats->ligplaats.getPrimitive() == null)
        .forEach(entity -> super.createPrimitive(entity, layerManager));
    }


    @Override
    protected void buildTags(BagMooringParcel ligplaats, Map<String, String> tags) {
        NLAddress address = ligplaats.getAddress();
        if (address != null) {
            createAddressTags(address, tags);
        }
        tags.put("source", "BAG");
        tags.put("source:date", ligplaats.getSourceDate());
        tags.put("ref:bag", BagEntityPrimitiveBuilder.formatBagId(ligplaats.getLigplaatsId()));
        // TODO Some municipalities draw the outline of the houseboat itself, whereas other draw the
        // outline of the parcel. There tends to be a shift to the latter, which is more conform the BAG standards.
        // In the beginning, we imported 'Ligplaats' objects as building=houseboat
        // Now we might consider to switch towards tagging that maps the outline of the parcel.
        // Some mappers tag the 'parcel' as 'Mooring = yes' 
        tags.put("building", "houseboat");
        tags.put("floating", "yes");
    }
}
