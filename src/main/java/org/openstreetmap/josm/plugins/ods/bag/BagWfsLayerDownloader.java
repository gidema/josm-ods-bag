package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingNeighboursEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.entities.managers.DataManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
    private final OdsModule module;
    private BagPrimitiveBuilder primitiveBuilder;

    LinkedList<AddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();

//    private final Filter pandFilter;
//    private final Filter vboFilter;
//    
//    { 
//        try {
//            pandFilter = CQL.toFilter("status <> 'Niet gerealiseerd pand' AND status <> 'Bouwvergunning verleend' " +
//                    "AND status <> 'Pand gesloopt' AND status <> 'Pand buiten gebruik'");
//            vboFilter = CQL.toFilter("status <> 'Verblijfsobject buiten gebruik' AND " +
//                    "status <> 'Niet gerealiseerd verblijfsobject' AND status <> 'Verblijfsobject ingetrokken'");
//        } catch (CQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public BagWfsLayerDownloader(OdsModule module) {
        super(module);
        this.module = module;
        addFeatureDownloader(createPandDownloader());
        addFeatureDownloader(createLigplaatsDownloader());
        addFeatureDownloader(createStandplaatsDownloader());
        addFeatureDownloader(createVerblijfsobjectDownloader());
        this.primitiveBuilder = new BagPrimitiveBuilder(module);
    }

    @Override
    public void process() {
        try {
            super.process();
            DataManager dataManager = module.getDataManager();
            matchAddressNodesToBuilding(dataManager);
            checkBuildingCompleteness(dataManager);
            distributeAddressNodes(dataManager);
            analyzeBuildingTypes(dataManager);
            findBuildingNeighbours(dataManager, getResponse());
            primitiveBuilder.run(getResponse());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private FeatureDownloader createPandDownloader() {
        return createBuildingDownloader("bag:pand");
    }
    
    private FeatureDownloader createLigplaatsDownloader() {
        return createBuildingDownloader("bag:ligplaats");
    }
    
    private FeatureDownloader createStandplaatsDownloader() {
        return createBuildingDownloader("bag:standplaats");
    }
    
    private FeatureDownloader createVerblijfsobjectDownloader() {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(module.getCrsUtil());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:verblijfsobject", "identificatie");
        GtDataSource dataSource = new GtDataSource(featureSource, null);
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            module.getDataManager().getOpenDataEntityStore(AddressNode.class));
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, featureType, "identificatie");
        GtDataSource dataSource = new GtDataSource(featureSource, null);
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            module.getDataManager().getOpenDataEntityStore(Building.class));
    }
    
    /**
     * Find a matching building for foreign addressNodes. 
     */
    private void matchAddressNodesToBuilding(DataManager dataManager) {
        OpenDataAddressNodeToBuildingMatcher matcher = new OpenDataAddressNodeToBuildingMatcher(dataManager);
        matcher.setUnmatchedAddressNodeHandler(unmatchedOpenDataAddressNodes::add);
        for(AddressNode addressNode :dataManager.getOsmEntityStore(AddressNode.class)) {
            matcher.matchAddressToBuilding(addressNode);
        }
    }
    
    private static void checkBuildingCompleteness(DataManager dataManager) {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) dataManager.getOpenDataEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingCompletenessEnricher(buildingStore);
        buildingStore.forEach(enricher);
    }
    
    private void distributeAddressNodes(DataManager dataManager) {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) dataManager.getOpenDataEntityStore(Building.class);
        Consumer<Building> enricher = new DistributeAddressNodes(module.getGeoUtil());
        buildingStore.forEach(enricher);
    }
    
    private static void analyzeBuildingTypes(DataManager dataManager) {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) dataManager.getOpenDataEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingTypeEnricher();
        buildingStore.forEach(enricher);
    }
    
    private void findBuildingNeighbours(DataManager dataManager, DownloadResponse response) {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) dataManager.getOpenDataEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingNeighboursEnricher(buildingStore, module.getGeoUtil());
        buildingStore.stream().filter(b->b.getDownloadResponse() == response).forEach(enricher);
    }
}
