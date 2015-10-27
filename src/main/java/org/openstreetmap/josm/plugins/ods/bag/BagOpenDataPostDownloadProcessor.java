package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingNeighboursEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;

public class BagOpenDataPostDownloadProcessor {
    private DataManager dataManager;
    private GeoUtil geoUtil;
    private DownloadResponse response;
    LinkedList<AddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    
    public BagOpenDataPostDownloadProcessor(DataManager dataManager, GeoUtil geoUtil) {
        super();
        this.geoUtil = geoUtil;
        this.dataManager = dataManager;
        this.response = response;
    }

    public void Run() {
        matchAddressNodesToBuilding();
        checkBuildingCompleteness();
        distributeAddressNodes();
        analyzeBuildingTypes();
        findBuildingNeighbours();
    }
    
    /**
     * Find a matching building for foreign addressNodes. 
     */
    private void matchAddressNodesToBuilding() {
        OpenDataAddressNodeToBuildingMatcher matcher = new OpenDataAddressNodeToBuildingMatcher(dataManager);
        matcher.setUnmatchedAddressNodeHandler(unmatchedOpenDataAddressNodes::add);
        dataManager.getAddressNodeManager().getOpenDataAddressNodes()
            .forEach(matcher.getAddressNodeConsumer());
    }
    
    private void checkBuildingCompleteness() {
        OpenDataBuildingStore buildingStore = dataManager.getBuildingManager().getOpenDataBuildings();
        Consumer<Building> enricher = new BuildingCompletenessEnricher(buildingStore);
        buildingStore.forEach(enricher);
    }
    
    private void distributeAddressNodes() {
        OpenDataBuildingStore buildingStore = dataManager.getBuildingManager().getOpenDataBuildings();
        Consumer<Building> enricher = new DistributeAddressNodes(geoUtil);
        buildingStore.forEach(enricher);
    }
    
    private void analyzeBuildingTypes() {
        OpenDataBuildingStore buildingStore = dataManager.getBuildingManager().getOpenDataBuildings();
        Consumer<Building> enricher = new BuildingTypeEnricher();
        buildingStore.forEach(enricher);
    }
    
    private void findBuildingNeighbours() {
        OpenDataBuildingStore buildingStore = dataManager.getBuildingManager().getOpenDataBuildings();
        Consumer<Building> enricher = new BuildingNeighboursEnricher(buildingStore, geoUtil);
        buildingStore.stream().filter(b->b.getDownloadResponse() == response).forEach(enricher);
    }
}
