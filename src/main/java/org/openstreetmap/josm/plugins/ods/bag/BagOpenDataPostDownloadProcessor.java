package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.EntitySource;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.foreign.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingNeighboursEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;

public class BagOpenDataPostDownloadProcessor {
    private DataManager dataManager;
    private GeoUtil geoUtil;
    private EntitySource entitySource;
    LinkedList<AddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();
    
    public BagOpenDataPostDownloadProcessor(DataManager dataManager, GeoUtil geoUtil, EntitySource entitySource) {
        super();
        this.geoUtil = geoUtil;
        this.dataManager = dataManager;
        this.entitySource = entitySource;
    }

    public List<Runnable> getPostProcessors() {
        return Arrays.asList(
            this::matchAddressNodesToBuilding,
            this::checkBuildingCompleteness,
            this::distributeAddressNodes,
            this::analyzeBuildingTypes,
            this::findBuildingNeighbours);
    }
    
    /**
     * Find a matching building for foreign addressNodes. 
     */
    private void matchAddressNodesToBuilding() {
        OpenDataAddressNodeToBuildingMatcher matcher = new OpenDataAddressNodeToBuildingMatcher(dataManager);
        matcher.setUnmatchedAddressNodeHandler(unmatchedOpenDataAddressNodes::add);
        dataManager.getAddressNodeManager().getForeignAddressNodes()
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
        buildingStore.stream().filter(b->b.getEntitySource() == entitySource).forEach(enricher);
    }
}
