package org.openstreetmap.josm.plugins.ods.bag.mapping;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Mapper;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooring;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooringStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public class MooringMapper implements Mapper {
    private final Map<Long, MooringPlot2BuildingMapping> mooringMappings = new HashMap<>();
    private final Map<Long, MooringPlotMapping> mooring2MooringMappings = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final OsmBagMooringStore osmBagMooringStore;
    private final BagMooringPlotStore bagMooringStore;
    private final List<BagMooringPlot> unmappedMoorings = new LinkedList<>();
    private final List<OsmBuilding> unmappedOsmBuildings = new LinkedList<>();

    public MooringMapper(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        osmBagMooringStore = context.getComponent(OsmBagMooringStore.class);
        bagMooringStore = context.getComponent(BagMooringPlotStore.class);
    }

    @Override
    public void run() {
        unmappedMoorings.clear();
        unmappedOsmBuildings.clear();
        for (BagMooringPlot mooring : bagMooringStore) {
            processOpenDataMooring(mooring);
        }
        analyze();
    }

    private void processOpenDataMooring(BagMooringPlot mooring) {
        if (!(mapToBuilding(mooring) || mapToMooring(mooring))) {
            unmappedMoorings.add(mooring);
        }
    }
    
    private boolean mapToBuilding(BagMooringPlot mooring) {
        Long id = mooring.getId();
        MooringPlot2BuildingMapping mapping = mooringMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(mooring);
            mooring.setMapping(mapping);
            return true;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            mapping = new MooringPlot2BuildingMapping(osmBuildings.get(0), mooring);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMapping(mapping);
                mapping.addOsmEntity(osmBuilding);
            }
            mooringMappings.put(id, mapping);
            return true;
        }
        return false;
    }

    private boolean mapToMooring(BagMooringPlot mooring) {
        Long id = mooring.getId();
        MooringPlotMapping mapping = mooring2MooringMappings.get(id);
        if (mapping != null) {
            mapping.addOpenDataEntity(mooring);
            mooring.setMapping(mapping);
            return true;
        }
        List<OsmBagMooring> osmMoorings = osmBagMooringStore.getMooringIdIndex().getAll(id);
        if (osmMoorings.size() > 0) {
            mapping = new MooringPlotMapping(osmMoorings.get(0), mooring);
            for (int i=1; i<osmMoorings.size() ; i++) {
                OsmBagMooring osmMooring = osmMoorings.get(i);
                osmMooring.setMapping(mapping);
                mapping.addOsmEntity(osmMooring);
            }
            mooring2MooringMappings.put(id, mapping);
            return true;
        }
        return false;
    }

    public void analyze() {
        for (Mapping<OsmBuilding, BagMooringPlot> mapping : mooringMappings.values()) {
            if (mapping.isSimple()) {
                mapping.analyze();
                mapping.refreshUpdateTags();
            }
        }
        for (MooringPlotMapping mapping : mooring2MooringMappings.values()) {
            if (mapping.isSimple()) {
                mapping.analyze();
                mapping.refreshUpdateTags();
            }
        }
        for (BagMooringPlot mooring: unmappedMoorings) {
            OsmPrimitive osm = mooring.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, mooring.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        mooringMappings.clear();
        unmappedOsmBuildings.clear();
    }
}
