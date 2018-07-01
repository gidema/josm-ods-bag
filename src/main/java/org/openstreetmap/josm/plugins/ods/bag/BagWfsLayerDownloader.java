package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.OdAddressNodesDistributer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDatasourceBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.InvalidQueryException;
import org.openstreetmap.josm.plugins.ods.matching.OdAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagWfsLayerDownloader extends OpenDataLayerDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?VERSION=2.0.0", 1000, 1000, 60000);
    private final BagPrimitiveBuilder primitiveBuilder;
    private final CRSUtil crsUtil;
    private final OdBuildingStore odBuildingStore;
    private final OdAddressNodeStore odAddressNodeStore;
    private final OdAddressNodeToBuildingMatcher odAddressNodeToBuildingMatcher;
    private final BuildingCompletenessEnricher buildingCompletenessEnricher;
    private final OdAddressNodesDistributer addressNodeDistributer;
    private final OdBuildingTypeEnricher buildingTypeEnricher;

    LinkedList<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();

    public BagWfsLayerDownloader(Context context) throws InitializationException {
        super(context);
        this.crsUtil = context.get(CRSUtil.class);
        this.odBuildingStore = context.get(OdBuildingStore.class);
        this.odAddressNodeStore = context.get(OdAddressNodeStore.class);
        this.odAddressNodeToBuildingMatcher = context.get(OdAddressNodeToBuildingMatcher.class);
        this.buildingCompletenessEnricher = context.get(BuildingCompletenessEnricher.class);
        this.addressNodeDistributer = context.get(OdAddressNodesDistributer.class);
        this.buildingTypeEnricher = context.get(OdBuildingTypeEnricher.class);
        addFeatureDownloader(createPandDownloader());
        addFeatureDownloader(createLigplaatsDownloader());
        addFeatureDownloader(createStandplaatsDownloader());
        addFeatureDownloader(createVerblijfsobjectDownloader());
        this.primitiveBuilder = new BagPrimitiveBuilder(context);
    }

    @Override
    public void process() {
        try {
            super.process();
            odAddressNodeToBuildingMatcher.run();
            buildingCompletenessEnricher.run();
            addressNodeDistributer.run();
            buildingTypeEnricher.run();
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
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(crsUtil);
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:verblijfsobject", "identificatie");
        featureSource.initialize();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "oppervlakte", "status", "gebruiksdoel",
                "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                "geometrie", "pandidentificatie");
        builder.setUniqueKey("identificatie", "pandidentificatie");
        builder.setPageSize(1000);
        //      Query query = new GroupByQuery(featureSource, properties, );
        GtDataSource dataSource = builder.build();
        return new GtDownloader<>(dataSource, crsUtil, entityBuilder,
                odAddressNodeStore);
    }

    private FeatureDownloader createBuildingDownloader(String featureType, List<String> properties) throws InvalidQueryException, InitializationException {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(crsUtil);
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, featureType, "identificatie");
        featureSource.initialize();
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties(properties);
        builder.setUniqueKey("identificatie");
        builder.setPageSize(1000);

        //       Query query = new GroupByQuery(featureSource, properties, Arrays.asList("identificatie"));
        GtDataSource dataSource = builder.build();
        FeatureDownloader downloader = new GtDownloader<>(dataSource, crsUtil, entityBuilder,
                odBuildingStore);
        // The original BAG import partially normalised the building geometries,
        // by making the (outer) rings clockwise. For fast comparison of geometries,
        // I choose to override the default normalisation here.
        downloader.setNormalisation(Normalisation.CLOCKWISE);
        return downloader;
    }
}
