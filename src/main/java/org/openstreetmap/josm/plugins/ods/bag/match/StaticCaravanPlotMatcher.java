package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class StaticCaravanPlotMatcher implements Matcher {
    private final Map<Long, StaticCaravanPlot2BuildingMatch> plot2BuildingMatches = new HashMap<>();
    private final Map<Long, StaticCaravanPlot2LanduseMatch> plot2LanduseMatches = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final OsmBagStaticCaravanPlotStore osmBagLanduseStore;
    private final BagStaticCaravanPlotStore bagStaticCaravanPlotStore;
    private final List<BagStaticCaravanPlot> unmatchedplots = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public StaticCaravanPlotMatcher(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        osmBagLanduseStore = context.getComponent(OsmBagStaticCaravanPlotStore.class);
        bagStaticCaravanPlotStore = context.getComponent(BagStaticCaravanPlotStore.class);
    }

    @Override
    public void run() {
        unmatchedplots.clear();
        unmatchedOsmBuildings.clear();
        for (BagStaticCaravanPlot plot : bagStaticCaravanPlotStore) {
            processOpenDataPlot(plot);
        }
        analyze();
    }

    private void processOpenDataPlot(BagStaticCaravanPlot plot) {
        if (!(matchToBuilding(plot) || matchToLanduse(plot))) {
            unmatchedplots.add(plot);
        }
    }
    
    private boolean matchToBuilding(BagStaticCaravanPlot plot) {
        Long id = plot.getStandplaatsId();
        StaticCaravanPlot2BuildingMatch match = plot2BuildingMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(plot);
            plot.setMatch(match);
            return true;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            match = new StaticCaravanPlot2BuildingMatch(osmBuildings.get(0), plot);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMatch(match);
                match.addOsmEntity(osmBuilding);
            }
            plot2BuildingMatches.put(id, match);
            return true;
        }
        return false;
    }

    private boolean matchToLanduse(BagStaticCaravanPlot plot) {
        Long id = plot.getStandplaatsId();
        StaticCaravanPlot2LanduseMatch match = plot2LanduseMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(plot);
            plot.setMatch(match);
            return true;
        }
        List<OsmBagStaticCaravanPlot> landuses = osmBagLanduseStore.getBagIdIndex().getAll(id);
        if (landuses.size() > 0) {
            match = new StaticCaravanPlot2LanduseMatch(landuses.get(0), plot);
            for (int i=1; i<landuses.size() ; i++) {
                OsmBagStaticCaravanPlot landuse = landuses.get(i);
                landuse.setMatch(match);
                match.addOsmEntity(landuse);
            }
            plot2LanduseMatches.put(id, match);
            return true;
        }
        return false;
    }

    public void analyze() {
        for (Match<OsmBuilding, BagStaticCaravanPlot> match : plot2BuildingMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (StaticCaravanPlot2LanduseMatch match : plot2LanduseMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (BagStaticCaravanPlot plot: unmatchedplots) {
            OsmPrimitive osm = plot.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, plot.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        plot2BuildingMatches.clear();
        plot2LanduseMatches.clear();
        unmatchedOsmBuildings.clear();
    }
}
