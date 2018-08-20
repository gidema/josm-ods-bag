package org.openstreetmap.josm.plugins.ods.bag.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagDuinoordAddressNodeParser;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagPdokLigplaatsParser;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagPdokPandParser;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagPdokStandplaatsParser;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagPdokVerblijfsobjectParser;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingToBuildingUnitRelation;
import org.openstreetmap.josm.plugins.ods.bag.relations.BuildingUnitToAddressNodeRelation;
import org.openstreetmap.josm.plugins.ods.bag.relations.OdBuildingUnitToAddressNodeBinder;
import org.openstreetmap.josm.plugins.ods.bag.relations.OdBuildingUnitToBuildingBinder;
import org.openstreetmap.josm.plugins.ods.bag.setup.BagModuleSetup.EntityStores;
import org.openstreetmap.josm.plugins.ods.binding.OdAddressNodeToBuildingBinder;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.entities.EntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.OdAddressNodesDistributer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdBoundaryManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDatasourceBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;
import org.openstreetmap.josm.tools.Logging;

public class BagOdSetup {
    private final CRSUtil crsUtil;
    private final GeoUtil geoUtil;
    private final EntityStores stores;
    private final OdLayerManager odLayerManager;
    private OpenDataLayerDownloader downloader;
    private final OdBoundaryManager boundaryManager;

    public BagOdSetup(OdLayerManager odLayerManager, EntityStores stores) {
        super();
        this.crsUtil = new CRSUtilProj4j();
        this.geoUtil = new GeoUtil();
        this.stores = stores;
        this.odLayerManager = odLayerManager;
        this.boundaryManager = new OdBoundaryManager(stores.odEntityStores());
        setup();
    }

    public OpenDataLayerDownloader getDownloader() {
        return downloader;
    }

    private void setup() {
        Hosts hosts = setupHosts();
        Relations relations = setupRelations();
        Parsers parsers = setupParsers(stores, relations);
        GtFeatureSources featureSources = setupFeatureSources(hosts);
        DataSources dataSources = setupDataSources(featureSources);
        FeatureDownloaders featureDownloaders = setupDownloaders(dataSources, parsers);

        List<Runnable> odProcessors = setupOdProcessors(relations);
        this.downloader = new OpenDataLayerDownloader(
                odLayerManager, featureDownloaders.all(), odProcessors, boundaryManager);
    }

    private Relations setupRelations() {
        Relations relations = new Relations();
        relations.buildingToBuildingUnit = new BuildingToBuildingUnitRelation();
        relations.buildingUnitToAddressNode = new BuildingUnitToAddressNodeRelation();
        return relations;
    }

    private Parsers setupParsers(EntityStores stores, Relations relations) {
        Parsers parsers = new Parsers();
        parsers.pand = new BagPdokPandParser(crsUtil, stores.odBuilding);
        parsers.ligplaats = new BagPdokLigplaatsParser(crsUtil,
                stores.odBuilding);
        parsers.standplaats = new BagPdokStandplaatsParser(crsUtil,
                stores.odBuilding);
        parsers.verblijfsobject = new BagPdokVerblijfsobjectParser(crsUtil,
                stores.odBuildingUnit, stores.odAddressNode,
                relations.buildingToBuildingUnit);
        parsers.duinoordAddress = new BagDuinoordAddressNodeParser(crsUtil,
                stores.odAddressNode,
                relations.buildingUnitToAddressNode);
        return parsers;
    }

    private Hosts setupHosts() {
        Hosts hosts = new Hosts();
        hosts.pdokBagWFS = new WFSHost("PDOK BAG WFS",
                "http://geodata.nationaalgeoregister.nl/bag/wfs?VERSION=2.0.0",
                1000, 1000, 60000);
        hosts.duinoordBagWFS = new WFSHost("DUINOORD BAG WFS",
                "https://duinoord.xs4all.nl/geoserver/wfs?VERSION=2.0.0", 1000,
                1000, 60000);
        return hosts;
    }

    private GtFeatureSources setupFeatureSources(Hosts host) {
        GtFeatureSources featureSources = new GtFeatureSources();
        featureSources.pand = new GtFeatureSource(host.pdokBagWFS, "bag:pand",
                "identificatie");
        featureSources.ligplaats = new GtFeatureSource(host.pdokBagWFS,
                "bag:ligplaats", "identificatie");
        featureSources.standplaats = new GtFeatureSource(host.pdokBagWFS,
                "bag:standplaats", "identificatie");
        featureSources.verblijfsobject = new GtFeatureSource(host.pdokBagWFS,
                "bag:verblijfsobject", "identificatie");
        featureSources.duinoordAdres = new GtFeatureSource(host.duinoordBagWFS,
                "bag:All_Addresses", "nummeraanduiding");
        return featureSources;
    }

    private DataSources setupDataSources(GtFeatureSources featureSources) {
        DataSources dataSources = new DataSources();
        dataSources.pand = createPandDataSource(featureSources.pand);
        dataSources.ligplaats = createLigplaatsDataSource(
                featureSources.ligplaats);
        dataSources.standplaats = createStandplaatsDataSource(
                featureSources.standplaats);
        dataSources.verblijfsobject = createVerblijfsobjectDataSource(
                featureSources.verblijfsobject);
        dataSources.duinoordAdres = createDuinoordAdresDataSource(
                featureSources.duinoordAdres);
        return dataSources;
    }

    private FeatureDownloaders setupDownloaders(DataSources dataSources,
            Parsers parsers) {
        FeatureDownloaders downloaders = new FeatureDownloaders();
        downloaders.pand = new GtDownloader(dataSources.pand, crsUtil,
                parsers.pand);
        downloaders.ligplaats = new GtDownloader(dataSources.ligplaats, crsUtil,
                parsers.ligplaats);
        downloaders.standplaats = new GtDownloader(dataSources.standplaats,
                crsUtil, parsers.standplaats);
        downloaders.verblijfsobject = new GtDownloader(
                dataSources.verblijfsobject, crsUtil, parsers.verblijfsobject);
        downloaders.duinoordAddress = new GtDownloader(
                dataSources.duinoordAdres, crsUtil, parsers.duinoordAddress);
        return downloaders;
    }

    private List<Runnable> setupOdProcessors(Relations relations) {
        List<Runnable> processors = new ArrayList<>(4);
        processors.add(new OdBuildingUnitToBuildingBinder(stores.odBuilding, stores.odBuildingUnit, relations.buildingToBuildingUnit));
        processors.add(new OdBuildingUnitToAddressNodeBinder(stores.odBuildingUnit, stores.odAddressNode,
                relations.buildingUnitToAddressNode));
        processors.add(new OdAddressNodeToBuildingBinder(stores.odAddressNode));
        processors.add(new BuildingCompletenessEnricher(stores.odBuilding));
        processors.add(new OdAddressNodesDistributer(stores.odBuilding, geoUtil));
        processors.add(new OdBuildingTypeEnricher(stores.odBuilding));
        processors.addAll(setupPrimitiveBuilders(stores));
        //        processors.add(new OdBuildingAligner(stores.odBuilding));
        return processors;
    }

    private List<EntityPrimitiveBuilder<?>> setupPrimitiveBuilders(EntityStores stores) {
        List<EntityPrimitiveBuilder<?>> builders = new ArrayList<>(2);

        builders.add(new BagBuildingEntityPrimitiveBuilder(odLayerManager, stores.odBuilding));
        builders.add(new BagAddressNodeEntityPrimitiveBuilder(odLayerManager, stores.odAddressNode));
        return builders;
    }

    private static GtDataSource createPandDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "bouwjaar", "status",
                "aantal_verblijfsobjecten", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    private static GtDataSource createLigplaatsDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "status", "openbare_ruimte",
                "huisnummer", "huisletter", "toevoeging", "postcode",
                "woonplaats", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    private static GtDataSource createStandplaatsDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "status", "openbare_ruimte",
                "huisnummer", "huisletter", "toevoeging", "postcode",
                "woonplaats", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    private static GtDataSource createVerblijfsobjectDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "oppervlakte", "status",
                "gebruiksdoel", "openbare_ruimte", "huisnummer", "huisletter",
                "toevoeging", "postcode", "woonplaats", "geometrie",
                "pandidentificatie");
        builder.setPageSize(1000);
        return builder.build();
    }

    private static GtDataSource createDuinoordAdresDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("verblijfsobject","geopunt","nummeraanduiding",
                "postcode","huisnummer","huisletter","huisnummertoevoeging",
                "nevenadres","openbareruimte","woonplaats");
        Filter filter;
        try {
            filter = CQL.toFilter("nevenadres = true");
            builder.setFilter(filter);
        } catch (CQLException e) {
            Logging.error(e);
            throw new RuntimeException(e);
        }
        builder.setPageSize(1000);
        return builder.build();
    }

    private class Parsers {
        BagPdokPandParser pand;
        BagPdokLigplaatsParser ligplaats;
        BagPdokStandplaatsParser standplaats;
        BagPdokVerblijfsobjectParser verblijfsobject;
        BagDuinoordAddressNodeParser duinoordAddress;

        public Parsers() {
        }
    }

    private class Relations {
        BuildingToBuildingUnitRelation buildingToBuildingUnit;
        BuildingUnitToAddressNodeRelation buildingUnitToAddressNode;

        public Relations() {
        }
    }

    private class Hosts {
        WFSHost pdokBagWFS;
        WFSHost duinoordBagWFS;

        Hosts() {
        }
    }

    private class GtFeatureSources {
        GtFeatureSource pand;
        GtFeatureSource standplaats;
        GtFeatureSource ligplaats;
        GtFeatureSource verblijfsobject;
        GtFeatureSource duinoordAdres;

        public GtFeatureSources() {
        }
    }

    private class DataSources {
        GtDataSource pand;
        GtDataSource standplaats;
        GtDataSource ligplaats;
        GtDataSource verblijfsobject;
        GtDataSource duinoordAdres;

        public DataSources() {
        }
    }

    private class FeatureDownloaders {
        FeatureDownloader pand;
        FeatureDownloader standplaats;
        FeatureDownloader ligplaats;
        FeatureDownloader verblijfsobject;
        FeatureDownloader duinoordAddress;

        public FeatureDownloaders() {
        }

        List<FeatureDownloader> all() {
            return Arrays.asList(pand, standplaats, ligplaats, verblijfsobject, duinoordAddress);
        }
    }

    static class LayerDownloaders {
        OsmLayerDownloader osm;
        OpenDataLayerDownloader od;
    }

    static class PrimitiveBuilders  {
        EntityPrimitiveBuilder<OdBuilding> building;
        EntityPrimitiveBuilder<OdAddressNode> addressNode;
    }
}
