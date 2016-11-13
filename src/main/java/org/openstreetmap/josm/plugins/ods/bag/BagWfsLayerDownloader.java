package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
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
import org.openstreetmap.josm.plugins.ods.geotools.GroupByQuery;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.InvalidQueryException;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?VERSION=1.1.0", 1000);
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

    public BagWfsLayerDownloader(OdsModule module) throws InitializationException {
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
//            findBuildingNeighbours(getResponse());
            primitiveBuilder.run(getResponse());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private FeatureDownloader createPandDownloader() throws InitializationException {
        List<String> properties = Arrays.asList("identificatie", "bouwjaar", "status", "aantal_verblijfsobjecten", "geometrie");
        return createBuildingDownloader("bag:pand", properties);
    }
    
    private FeatureDownloader createLigplaatsDownloader() throws InitializationException {
        List<String> properties = Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
            "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        return createBuildingDownloader("bag:ligplaats", properties);
    }
    
    private FeatureDownloader createStandplaatsDownloader() throws InitializationException {
        List<String> properties = Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
            "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        return createBuildingDownloader("bag:standplaats", properties);
    }
    
    private FeatureDownloader createVerblijfsobjectDownloader() throws InitializationException {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(module.getCrsUtil());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:verblijfsobject", "identificatie");
        featureSource.initialize();
        List<String> properties = Arrays.asList("identificatie", "oppervlakte", "status", "gebruiksdoel",
                "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                "geometrie", "pandidentificatie");
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie", "pandidentificatie"));
        GtDataSource dataSource = new GtDataSource(featureSource, query);
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(AddressNode.class));
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType, List<String> properties) throws InvalidQueryException, InitializationException {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, featureType, "identificatie");
        featureSource.initialize();
        Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        GtDataSource dataSource = new GtDataSource(featureSource, query);
        FeatureDownloader downloader = new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder, 
            layerManager.getEntityStore(Building.class));
        // The original BAG import partially normalised the building geometries,
        // by making the (outer) rings clockwise. For fast comparison of geometries,
        // I choose to override the default normalisation here.
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
