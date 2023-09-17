package org.openstreetmap.josm.plugins.ods.bag.mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Mapper;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public class StaticCaravanPlotMapper implements Mapper {
    private final Map<Long, StaticCaravanPlot2BuildingMapping> plot2BuildingMappings = new HashMap<>();
    private final Map<Long, StaticCaravanPlot2LanduseMapping> plot2LanduseMappings = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final OsmBagStaticCaravanPlotStore osmBagLanduseStore;
    private final BagStaticCaravanPlotStore bagStaticCaravanPlotStore;
    private final List<BagStaticCaravanPlot> unmappedplots = new LinkedList<>();
    private final List<OsmBuilding> unmappedOsmBuildings = new LinkedList<>();

    public StaticCaravanPlotMapper(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        osmBagLanduseStore = context.getComponent(OsmBagStaticCaravanPlotStore.class);
        bagStaticCaravanPlotStore = context.getComponent(BagStaticCaravanPlotStore.class);
    }

    @Override
    public void run() {
        unmappedplots.clear();
        unmappedOsmBuildings.clear();
        for (BagStaticCaravanPlot plot : bagStaticCaravanPlotStore) {
            processOpenDataPlot(plot);
        }
        analyze();
    }

    private void processOpenDataPlot(BagStaticCaravanPlot plot) {
        if (!(mapToBuilding(plot) || mapToLanduse(plot))) {
            unmappedplots.add(plot);
        }
    }
    
    private boolean mapToBuilding(BagStaticCaravanPlot plot) {
        Long id = plot.getId();
        StaticCaravanPlot2BuildingMapping mapping = plot2BuildingMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(plot);
            plot.setMapping(mapping);
            return true;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            mapping = new StaticCaravanPlot2BuildingMapping(osmBuildings.get(0), plot);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMapping(mapping);
                mapping.addOsmEntity(osmBuilding);
            }
            plot2BuildingMappings.put(id, mapping);
            return true;
        }
        return false;
    }

    private boolean mapToLanduse(BagStaticCaravanPlot plot) {
        Long id = plot.getId();
        StaticCaravanPlot2LanduseMapping mapping = plot2LanduseMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(plot);
            plot.setMapping(mapping);
            return true;
        }
        List<OsmBagStaticCaravanPlot> landuses = osmBagLanduseStore.getBagIdIndex().getAll(id);
        if (landuses.size() > 0) {
            mapping = new StaticCaravanPlot2LanduseMapping(landuses.get(0), plot);
            for (int i=1; i<landuses.size() ; i++) {
                OsmBagStaticCaravanPlot landuse = landuses.get(i);
                landuse.setMapping(mapping);
                mapping.addOsmEntity(landuse);
            }
            plot2LanduseMappings.put(id, mapping);
            return true;
        }
        return false;
    }

    public void analyze() {
        for (Mapping<OsmBuilding, BagStaticCaravanPlot> mapping : plot2BuildingMappings.values()) {
            if (mapping.isSimple()) {
                mapping.analyze();
                mapping.refreshUpdateTags();
            }
        }
        for (StaticCaravanPlot2LanduseMapping mapping : plot2LanduseMappings.values()) {
            if (mapping.isSimple()) {
                mapping.analyze();
                mapping.refreshUpdateTags();
            }
        }
        for (BagStaticCaravanPlot plot: unmappedplots) {
            OsmPrimitive osm = plot.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, plot.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        plot2BuildingMappings.clear();
        plot2LanduseMappings.clear();
        unmappedOsmBuildings.clear();
    }
}
