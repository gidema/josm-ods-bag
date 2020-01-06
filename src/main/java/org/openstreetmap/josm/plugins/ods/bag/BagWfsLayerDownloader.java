package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;

import org.opengis.feature.type.FeatureType;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.gt.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDatasourceBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityFactory;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.InvalidQueryException;
import org.openstreetmap.josm.plugins.ods.matching.OpenDataAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.od.GtEntityFactoryFactory;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs/v1_1?request=getCapabilities&service=WFS&version=1.1.0", 1000, 1000, 60000);
    private final OdsModule module;
    private final OdLayerManager layerManager;
    private final BagPrimitiveBuilder primitiveBuilder;

    LinkedList<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();

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

    private FeatureDownloader createVerblijfsobjectDownloader() throws InitializationException {
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:verblijfsobject", "identificatie");
        featureSource.initialize();
        FeatureType featureType = featureSource.getFeatureType();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties(Arrays.asList("identificatie", "oppervlakte", "status", "gebruiksdoel",
                "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                "geometrie", "pandidentificatie"));
        builder.setUniqueKey(Arrays.asList("identificatie", "pandidentificatie"));
        builder.setPageSize(1000);
        //      Query query = new GroupByQuery(featureSource, properties, );
        GtDataSource dataSource = builder.build();
        GtEntityFactory<OdAddressNode> entityBuilder = GtEntityFactoryFactory.create(
             featureType.getName(), OdAddressNode.class);
        return new GtDownloader<>(dataSource, module.getCrsUtil(), entityBuilder,
                layerManager.getEntityStore(OdAddressNode.class));
    }

    private FeatureDownloader createPandDownloader() throws InvalidQueryException, InitializationException {
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:pand", "identificatie");
        featureSource.initialize();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties(Arrays.asList("identificatie", "bouwjaar", "status", "aantal_verblijfsobjecten",
                "geometrie"));
        builder.setUniqueKey("identificatie");
        builder.setPageSize(1000);

        //       Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        GtDataSource dataSource = builder.build();
        GtEntityFactory<OdBuilding> entityBuilder = GtEntityFactoryFactory.create(
                featureSource.getFeatureType().getName(), OdBuilding.class);
        FeatureDownloader downloader = new GtDownloader<OdBuilding>(dataSource, module.getCrsUtil(), entityBuilder,
                layerManager.getEntityStore(OdBuilding.class));
        // The original BAG import partially normalised the building geometries,
        // by making the (outer) rings clockwise. For fast comparison of geometries,
        // I choose to override the default normalisation here.
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }

    private FeatureDownloader createLigplaatsDownloader() throws InvalidQueryException, InitializationException {
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:ligplaats", "identificatie");
        featureSource.initialize();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties(Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie"));
        builder.setUniqueKey("identificatie");
        builder.setPageSize(1000);

        //       Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        GtDataSource dataSource = builder.build();
        GtEntityFactory<OdBuilding> entityBuilder = GtEntityFactoryFactory.create(
                featureSource.getFeatureType().getName(), OdBuilding.class);
        FeatureDownloader downloader = new GtDownloader<OdBuilding>(dataSource, module.getCrsUtil(), entityBuilder,
                layerManager.getEntityStore(OdBuilding.class));
        // The original BAG import partially normalised the building geometries,
        // by making the (outer) rings clockwise. For fast comparison of geometries,
        // I choose to override the default normalisation here.
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }

    private FeatureDownloader createStandplaatsDownloader() throws InvalidQueryException, InitializationException {
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:standplaats", "identificatie");
        featureSource.initialize();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties(Arrays.asList("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie"));
        builder.setUniqueKey("identificatie");
        builder.setPageSize(1000);

        //       Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        GtDataSource dataSource = builder.build();
        GtEntityFactory<OdBuilding> entityBuilder = GtEntityFactoryFactory.create(
                featureSource.getFeatureType().getName(), OdBuilding.class);
        FeatureDownloader downloader = new GtDownloader<OdBuilding>(dataSource, module.getCrsUtil(), entityBuilder,
                layerManager.getEntityStore(OdBuilding.class));
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
        for(OdAddressNode addressNode : layerManager.getEntityStore(OdAddressNode.class)) {
            matcher.matchAddressToBuilding(addressNode);
        }
    }

    private void checkBuildingCompleteness() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(OdBuilding.class);
        Consumer<OdBuilding> enricher = new BuildingCompletenessEnricher(buildingStore);
        buildingStore.forEach(enricher);
    }

    private void distributeAddressNodes() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(OdBuilding.class);
        Consumer<OdBuilding> enricher = new DistributeAddressNodes(module.getGeoUtil());
        buildingStore.forEach(enricher);
    }

    private void analyzeBuildingTypes() {
        OpenDataBuildingStore buildingStore = (OpenDataBuildingStore) layerManager.getEntityStore(OdBuilding.class);
        Consumer<OdBuilding> enricher = new BuildingTypeEnricher();
        buildingStore.forEach(enricher);
    }
}
