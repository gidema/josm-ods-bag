package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import java.util.Map;

import org.openstreetmap.josm.plugins.ods.bag.BagImportPlugin;
import org.openstreetmap.josm.plugins.ods.bag.BagPreferences;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;

public class BagStaticCaravanPlotPrimitiveBuilder extends BagEntityPrimitiveBuilder<BagStaticCaravanPlot> {
    private final BagPreferences preferences;
    
    public BagStaticCaravanPlotPrimitiveBuilder() {
        super();
        this.preferences = BagImportPlugin.getPreferences();
    }

    @Override
    public void run(OdsContext context) {
        OdLayerManager layerManager = context.getComponent(OdLayerManager.class);
        BagStaticCaravanPlotStore standplaatsStore = context.getComponent(BagStaticCaravanPlotStore.class);
        standplaatsStore.stream()
        .filter(standplaats->standplaats.getPrimitive() == null)
        .forEach(entity -> super.createPrimitive(entity, layerManager));
    }

    @Override
    protected void buildTags(BagStaticCaravanPlot standplaats, Map<String, String> tags) {
        if (!preferences.isStaticCaravanAddressNode()) {
            NLAddress address = standplaats.getMainAddress();
            if (address != null) {
                createAddressTags(address, tags);
            }
        }
        tags.put("source", "BAG");
        tags.put("source:date", standplaats.getSourceDate());
        tags.put("ref:bag", BagEntityPrimitiveBuilder.formatBagId(standplaats.getId()));
        // TODO Some municipalities draw the outline of the static caravan itself, whereas other draw the
        // outline of the plot. There tends to be a shift to the latter, which is more conform the BAG standards.
        // In the beginning, we imported 'Standplaats' objects as building=static_caravan
        // Now we might consider to switch towards tagging that maps the outline of the plot.
        // Some mappers tag the 'plot' as 'landuse = static_caravan' and/or place=plot + plot=static_caravan.
        // As the plots tend to be a lot bigger than the caravans, we might consider the area of the geometry to
        // differentiate between them. 
        tags.put("place", "plot");
        tags.put("plot", "static_caravan");
        if (preferences.isStaticCaravanLanduse()) {
            tags.put("landuse", "static_caravan");
        }
    }
}
