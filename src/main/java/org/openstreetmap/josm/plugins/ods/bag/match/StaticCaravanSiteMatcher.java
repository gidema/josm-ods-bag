package org.openstreetmap.josm.plugins.ods.bag.match;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.Matcher;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanParcel;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagLanduse;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagLanduseStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanParcelStore;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.matching.Match;

public class StaticCaravanSiteMatcher implements Matcher {
    private final Map<Long, StaticCaravanSite2BuildingMatch> site2BuildingMatches = new HashMap<>();
    private final Map<Long, StaticCaravanSite2LanduseMatch> site2LanduseMatches = new HashMap<>();
    private final OsmBuildingStore osmBuildingStore;
    private final OsmBagLanduseStore osmBagLanduseStore;
    private final BagStaticCaravanParcelStore bagStaticCaravanSiteStore;
    private final List<BagStaticCaravanParcel> unmatchedSites = new LinkedList<>();
    private final List<OsmBuilding> unmatchedOsmBuildings = new LinkedList<>();

    public StaticCaravanSiteMatcher(OdsContext context) {
        super();
        osmBuildingStore = context.getComponent(OsmBuildingStore.class);
        osmBagLanduseStore = context.getComponent(OsmBagLanduseStore.class);
        bagStaticCaravanSiteStore = context.getComponent(BagStaticCaravanParcelStore.class);
    }

    @Override
    public void run() {
        unmatchedSites.clear();
        unmatchedOsmBuildings.clear();
        for (BagStaticCaravanParcel site : bagStaticCaravanSiteStore) {
            processOpenDataSite(site);
        }
        analyze();
    }

    private void processOpenDataSite(BagStaticCaravanParcel site) {
        if (!(matchToBuilding(site) || matchToLanduse(site))) {
            unmatchedSites.add(site);
        }
    }
    
    private boolean matchToBuilding(BagStaticCaravanParcel site) {
        Long id = site.getStandplaatsId();
        StaticCaravanSite2BuildingMatch match = site2BuildingMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(site);
            site.setMatch(match);
            return true;
        }
        List<OsmBuilding> osmBuildings = osmBuildingStore.getBuildingIdIndex().getAll(id);
        if (osmBuildings.size() > 0) {
            match = new StaticCaravanSite2BuildingMatch(osmBuildings.get(0), site);
            for (int i=1; i<osmBuildings.size() ; i++) {
                OsmBuilding osmBuilding = osmBuildings.get(i);
                osmBuilding.setMatch(match);
                match.addOsmEntity(osmBuilding);
            }
            site2BuildingMatches.put(id, match);
            return true;
        }
        return false;
    }

    private boolean matchToLanduse(BagStaticCaravanParcel site) {
        Long id = site.getStandplaatsId();
        StaticCaravanSite2LanduseMatch match = site2LanduseMatches.get(id);
        if (match != null) {
            match.addOpenDataEntity(site);
            site.setMatch(match);
            return true;
        }
        List<OsmBagLanduse> landuses = osmBagLanduseStore.getBagIdIndex().getAll(id);
        if (landuses.size() > 0) {
            match = new StaticCaravanSite2LanduseMatch(landuses.get(0), site);
            for (int i=1; i<landuses.size() ; i++) {
                OsmBagLanduse landuse = landuses.get(i);
                landuse.setMatch(match);
                match.addOsmEntity(landuse);
            }
            site2LanduseMatches.put(id, match);
            return true;
        }
        return false;
    }

    public void analyze() {
        for (Match<OsmBuilding, BagStaticCaravanParcel> match : site2BuildingMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (StaticCaravanSite2LanduseMatch match : site2LanduseMatches.values()) {
            if (match.isSimple()) {
                match.analyze();
                match.updateMatchTags();
            }
        }
        for (BagStaticCaravanParcel site: unmatchedSites) {
            OsmPrimitive osm = site.getPrimitive();
            if (osm != null) {
                osm.put(ODS.KEY.IDMATCH, "false");
                osm.put(ODS.KEY.STATUS, site.getStatus().toString());
            }
        }
    }

    @Override
    public void reset() {
        site2BuildingMatches.clear();
        site2LanduseMatches.clear();
        unmatchedOsmBuildings.clear();
    }
}
