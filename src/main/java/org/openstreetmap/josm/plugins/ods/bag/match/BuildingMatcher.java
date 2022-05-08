package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class BuildingMatcher implements Matcher {
    private final Map<Long, BuildingMatch> buildingMatches = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final BagBuildingStore odBuildingStore;
    private final List<OsmBuilding> unidentifiedOsmBuildings = new LinkedList<>();
    private final List<BagBuilding> unmatchedOpenDataBuildings = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public BuildingMatcher(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        odBuildingStore = context.getComponent(BagBuildingStore.class);
    }

    @Override
    public void run() {
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
        for (BagBuilding building : odBuildingStore) {
            processOpenDataBuilding(building);
        }
        for (OsmBuilding building : osmBuildingStore) {
            processOsmBuilding(building);
        }
        analyze();
    }

    private void processOpenDataBuilding(BagBuilding odBuilding) {
        Long id = (Long) odBuilding.getBuildingId();
        BuildingMatch match = buildingMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(odBuilding);
            odBuilding.setMatch(match);
            return;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            match = new BuildingMatch(osmBuildings.get(0), odBuilding);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMatch(match);
                match.addOsmEntity(osmBuilding);
            }
            buildingMatches.put(id, match);
        } else {
            unmatchedOpenDataBuildings.add(odBuilding);
        }
    }

    private void processOsmBuilding(OsmBuilding osmBuilding) {
        Long id = osmBuilding.getBuildingId();
        if (id == null) {
            unidentifiedOsmBuildings.add(osmBuilding);
            return;
        }
        BagBuilding odBuilding = odBuildingStore.getPrimaryIndex().get(id);
        if (odBuilding != null) {
            BuildingMatch match = new BuildingMatch(osmBuilding, odBuilding);
            buildingMatches.put(id, match);
        } else {
            unmatchedOsmBuildings.add(osmBuilding);
        }
    }

    public void analyze() {
        for (Match<OsmBuilding, BagBuilding> match : buildingMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (BagBuilding building: unmatchedOpenDataBuildings) {
            OsmPrimitive osm = building.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, building.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        buildingMatches.clear();
        unidentifiedOsmBuildings.clear();
        unmatchedOpenDataBuildings.clear();
        unmatchedOsmBuildings.clear();
    }
}
