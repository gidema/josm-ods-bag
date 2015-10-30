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
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
    private final OdsModule module;
    private final OpenDataLayerManager layerManager;
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
        this.layerManager = module.getOpenDataLayerManager();
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
            matchAddressNodesToBuilding();
            checkBuildingCompleteness();
            distributeAddressNodes();
            analyzeBuildingTypes();
            findBuildingNeighbours(getResponse());
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
            layerManager.getEntityStore(AddressNode.class));
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, featureType, "identificatie");
        GtDataSource dataSource = new GtDataSource(featureSource, null);
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(Building.class));
    }
    
    /**
     * Find a matching building for foreign addressNodes. 
     */
    private void matchAddressNodesToBuilding() {
        OpenDataAddressNodeToBuildingMatcher matcher = new OpenDataAddressNodeToBuildingMatcher(module);
        matcher.setUnmatchedAddressNodeHandler(unmatchedOpenDataAddressNodes::add);
        for(AddressNode addressNode : layerManager.getEntityStore(AddressNode.class)) {
            matcher.matchAddressToBuilding(addressNode);
        }
    }
    
    private void checkBuildingCompleteness() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingCompletenessEnricher(buildingStore);
        buildingStore.forEach(enricher);
    }
    
    private void distributeAddressNodes() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(Building.class);
        Consumer<Building> enricher = new DistributeAddressNodes(module.getGeoUtil());
        buildingStore.forEach(enricher);
    }
    
    private void analyzeBuildingTypes() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingTypeEnricher();
        buildingStore.forEach(enricher);
    }
    
    private void findBuildingNeighbours(DownloadResponse response) {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(Building.class);
        Consumer<Building> enricher = new BuildingNeighboursEnricher(buildingStore, module.getGeoUtil());
        buildingStore.stream().filter(b->b.getDownloadResponse() == response).forEach(enricher);
    }
}
