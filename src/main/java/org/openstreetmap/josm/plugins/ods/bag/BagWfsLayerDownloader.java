package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.Normalisation;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagPdokBuildingUnitBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.bag.relations.OdBuildingUnitToBuildingBinder;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;
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
    private final WFSHost pdokWfsHost = new WFSHost("PDOK BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs?VERSION=2.0.0", 1000, 1000, 60000);
    private final WFSHost duinoordWfsHost = new WFSHost("DUINOORD BAG WFS", "https://duinoord.xs4all.nl/geoserver/wfs?VERSION=2.0.0", 1000, 1000, 60000);
    private final BagPrimitiveBuilder primitiveBuilder;
    private final CRSUtil crsUtil;
    private final OdBuildingStore odBuildingStore;
    private final OdBuildingUnitStore odBuildingUnitStore;
    private final OdAddressNodeStore odAddressNodeStore;
    private final OdAddressNodeToBuildingMatcher odAddressNodeToBuildingMatcher;
    private final OdBuildingUnitToBuildingBinder odBuildingUnitToBuildingBinder;
    private final BuildingCompletenessEnricher buildingCompletenessEnricher;
    private final OdAddressNodesDistributer addressNodeDistributer;
    private final OdBuildingTypeEnricher buildingTypeEnricher;

    LinkedList<OdAddressNode> unmatchedOpenDataAddressNodes = new LinkedList<>();

    public BagWfsLayerDownloader(Context context) throws InitializationException {
        super(context);
        this.crsUtil = context.get(CRSUtil.class);
        this.odBuildingStore = context.get(OdBuildingStore.class);
        this.odBuildingUnitStore = context.get(OdBuildingUnitStore.class);
        this.odAddressNodeStore = context.get(OdAddressNodeStore.class);
        this.odBuildingUnitToBuildingBinder = context.get(OdBuildingUnitToBuildingBinder.class);
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
            odBuildingUnitToBuildingBinder.run();
            //            odAddressNodeToBuildingMatcher.run();
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
        BagPdokBuildingUnitBuilder entityBuilder = new BagPdokBuildingUnitBuilder(crsUtil);
        GtFeatureSource featureSource = new GtFeatureSource(pdokWfsHost, "bag:verblijfsobject", "identificatie");
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
                odBuildingUnitStore);
    }

    private FeatureDownloader createBuildingDownloader(String featureType, List<String> properties) throws InvalidQueryException, InitializationException {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(crsUtil);
        GtFeatureSource featureSource = new GtFeatureSource(pdokWfsHost, featureType, "identificatie");
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
