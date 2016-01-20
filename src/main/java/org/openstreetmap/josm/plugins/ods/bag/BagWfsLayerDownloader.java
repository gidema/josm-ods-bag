package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;

import exceptions.OdsException;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    
    private final OdsModule module;
    private final OdsModuleConfiguration configuration;
    private OpenDataLayerManager layerManager;
    private BagPrimitiveBuilder primitiveBuilder;

    LinkedList<AddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();

    public BagWfsLayerDownloader(OdsModule module) {
        super(module);
        this.module = module;
        this.configuration = module.getConfiguration();
    }
    
    @Override
    public void initialize() throws OdsException {
        this.layerManager = module.getOpenDataLayerManager();
        addFeatureDownloader(createBuildingDownloader("bag:pand"));
//        addFeatureDownloader(createDemolishedBuildingsDownloader());
        addFeatureDownloader(createBuildingDownloader("bag:ligplaats"));
        addFeatureDownloader(createBuildingDownloader("bag:standplaats"));
        addFeatureDownloader(createVerblijfsobjectDownloader());
        this.primitiveBuilder = new BagPrimitiveBuilder(module);
    }

    
    @Override
    protected Collection<? extends Host> getHosts() {
        return configuration.getHosts();
    }

    @Override
    public void process() {
        try {
            super.process();
            matchAddressNodesToBuilding();
            checkBuildingCompleteness();
            distributeAddressNodes();
            analyzeBuildingTypes();
//            findBuildingNeighbours(getResponse());
            primitiveBuilder.run(getResponse());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//    private FeatureDownloader createPandDownloader() throws InitializationException {
//        return createBuildingDownloader("bag:pand");
//    }
//    
//    private FeatureDownloader createLigplaatsDownloader() throws InitializationException {
//        return createBuildingDownloader("bag:ligplaats");
//    }
//    
//    private FeatureDownloader createStandplaatsDownloader() throws InitializationException {
//        return createBuildingDownloader("bag:standplaats");
//    }
//    
    private FeatureDownloader createVerblijfsobjectDownloader() throws OdsException {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(module.getCrsUtil());
        OdsDataSource dataSource = configuration.getDataSource("bag:verblijfsobject");
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(AddressNode.class));
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) throws OdsException {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil());
        OdsDataSource dataSource = configuration.getDataSource(featureType);
        FeatureDownloader downloader = new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(Building.class));
        /*
         *  The original BAG import partially normalised the building geometries,
         * by making the (outer) rings clockwise. For fast comparison of geometries,
         * I choose to override the default normalisation here.
         */
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }
    
    private FeatureDownloader createDemolishedBuildingsDownloader() throws OdsException {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil());
        OdsDataSource dataSource = configuration.getDataSource("osm_bag:buildingdestroyed_osm");
        FeatureDownloader downloader = new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(Building.class));
        /*
         * The original BAG import partially normalised the building geometries,
         * by making the (outer) rings clockwise. For fast comparison of geometries,
         * I choose to override the default normalisation here.
         */
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
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
}
