package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooring;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooringStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class MooringMatcher implements Matcher {
    private final Map<Long, MooringPlot2BuildingMatch> mooringMatches = new HashMap<>();
    private final Map<Long, MooringPlotMatch> mooring2MooringMatches = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final OsmBagMooringStore osmBagMooringStore;
    private final BagMooringPlotStore bagMooringStore;
    private final List<BagMooringPlot> unmatchedMoorings = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public MooringMatcher(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        osmBagMooringStore = context.getComponent(OsmBagMooringStore.class);
        bagMooringStore = context.getComponent(BagMooringPlotStore.class);
    }

    @Override
    public void run() {
        unmatchedMoorings.clear();
        unmatchedOsmBuildings.clear();
        for (BagMooringPlot mooring : bagMooringStore) {
            processOpenDataMooring(mooring);
        }
        analyze();
    }

    private void processOpenDataMooring(BagMooringPlot mooring) {
        if (!(matchToBuilding(mooring) || matchToMooring(mooring))) {
            unmatchedMoorings.add(mooring);
        }
    }
    
    private boolean matchToBuilding(BagMooringPlot mooring) {
        Long id = mooring.getLigplaatsId();
        MooringPlot2BuildingMatch match = mooringMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(mooring);
            mooring.setMatch(match);
            return true;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            match = new MooringPlot2BuildingMatch(osmBuildings.get(0), mooring);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMatch(match);
                match.addOsmEntity(osmBuilding);
            }
            mooringMatches.put(id, match);
            return true;
        }
        return false;
    }

    private boolean matchToMooring(BagMooringPlot mooring) {
        Long id = mooring.getLigplaatsId();
        MooringPlotMatch match = mooring2MooringMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(mooring);
            mooring.setMatch(match);
            return true;
        }
        List<OsmBagMooring> osmMoorings = osmBagMooringStore.getMooringIdIndex().getAll(id);
        if (osmMoorings.size() > 0) {
            match = new MooringPlotMatch(osmMoorings.get(0), mooring);
            for (int i=1; i<osmMoorings.size() ; i++) {
                OsmBagMooring osmMooring = osmMoorings.get(i);
                osmMooring.setMatch(match);
                match.addOsmEntity(osmMooring);
            }
            mooring2MooringMatches.put(id, match);
            return true;
        }
        return false;
    }

    public void analyze() {
        for (Match<OsmBuilding, BagMooringPlot> match : mooringMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (MooringPlotMatch match : mooring2MooringMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (BagMooringPlot mooring: unmatchedMoorings) {
            OsmPrimitive osm = mooring.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, mooring.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        mooringMatches.clear();
        unmatchedOsmBuildings.clear();
    }
}
