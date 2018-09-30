package org.openstreetmap.josm.plugins.ods.bag.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagDuinoordAddressMissingParser;
import org.openstreetmap.josm.plugins.ods.bag.gt.parsing.BagDuinoordAddressWithdrawnParser;
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
    private final GeoUtil geoUtil;
    private final EntityStores stores;
    private final OdLayerManager odLayerManager;
    private OpenDataLayerDownloader downloader;
    private final OdBoundaryManager boundaryManager;

    public BagOdSetup(OdLayerManager odLayerManager, EntityStores stores) {
        super();
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
        Hosts hosts = new Hosts();
        Relations relations = new Relations();
        Parsers parsers = new Parsers(stores, relations);
        GtFeatureSources featureSources = new GtFeatureSources(hosts);
        DataSources dataSources = new DataSources(featureSources);
        FeatureDownloaders featureDownloaders = new FeatureDownloaders(dataSources, parsers);

        List<Runnable> odProcessors = setupOdProcessors(relations);
        this.downloader = new OpenDataLayerDownloader(
                odLayerManager, featureDownloaders.all(), odProcessors, boundaryManager);
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

    static GtDataSource createPandDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "bouwjaar", "status",
                "aantal_verblijfsobjecten", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    static GtDataSource createLigplaatsDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "status", "openbare_ruimte",
                "huisnummer", "huisletter", "toevoeging", "postcode",
                "woonplaats", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    static GtDataSource createStandplaatsDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("identificatie", "status", "openbare_ruimte",
                "huisnummer", "huisletter", "toevoeging", "postcode",
                "woonplaats", "geometrie");
        builder.setPageSize(1000);
        return builder.build();
    }

    static GtDataSource createVerblijfsobjectDataSource(
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

    static GtDataSource createDuinoordAdresDataSource(
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
        builder.setPageSize(0);
        return builder.build();
    }

    static GtDataSource createDuinoordAddressMissingDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("nummeraanduiding","geopunt","postcode",
                "huisnummer","huisletter","huisnummertoevoeging",
                "straat","woonplaats","nevenadres","pandidentificatie");
        Filter filter;
        try {
            filter = CQL.toFilter("nevenadres = true");
            builder.setFilter(filter);
        } catch (CQLException e) {
            Logging.error(e);
            throw new RuntimeException(e);
        }
        builder.setPageSize(0);
        return builder.build();
    }

    static GtDataSource createDuinoordAddressWithdrawnDataSource(
            GtFeatureSource featureSource) {
        GtDatasourceBuilder builder = new GtDatasourceBuilder();
        builder.setFeatureSource(featureSource);
        builder.setProperties("nummeraanduiding","geopunt","postcode",
                "huisnummer","huisletter","huisnummertoevoeging",
                "straat","woonplaats","nevenadres","osm_id");
        builder.setPageSize(0);
        return builder.build();
    }

    private class Parsers {
        final BagPdokPandParser pand;
        final BagPdokLigplaatsParser ligplaats;
        final BagPdokStandplaatsParser standplaats;
        final BagPdokVerblijfsobjectParser verblijfsobject;
        //        BagDuinoordAddressNodeParser duinoordAddress;
        final BagDuinoordAddressMissingParser duinoordAddressMissing;
        final BagDuinoordAddressWithdrawnParser duinoordAddressWithdrawn;

        public Parsers(EntityStores stores, Relations relations) {
            this.pand = new BagPdokPandParser(stores.odBuilding);
            this.ligplaats = new BagPdokLigplaatsParser(stores.odLigplaats);
            this.standplaats = new BagPdokStandplaatsParser(stores.odStandplaats);
            this.verblijfsobject = new BagPdokVerblijfsobjectParser(stores.odBuildingUnit, stores.odAddressNode,
                    relations.buildingToBuildingUnit);
            //        this.duinoordAddress = new BagDuinoordAddressNodeParser(crsUtil,
            //                stores.odAddressNode,
            //                relations.buildingUnitToAddressNode);
            this.duinoordAddressMissing = new BagDuinoordAddressMissingParser(stores.odAddressNode,
                    relations.buildingUnitToAddressNode);
            this.duinoordAddressWithdrawn = new BagDuinoordAddressWithdrawnParser(stores.odAddressNode);
        }
    }

    private class Relations {
        final BuildingToBuildingUnitRelation buildingToBuildingUnit = new BuildingToBuildingUnitRelation();
        final BuildingUnitToAddressNodeRelation buildingUnitToAddressNode = new BuildingUnitToAddressNodeRelation();

        public Relations() {
        }
    }

    private class Hosts {
        final WFSHost pdokBagWFS = new WFSHost("PDOK BAG WFS",
                "http://geodata.nationaalgeoregister.nl/bag/wfs?VERSION=2.0.0",
                1000, 1000, 60000);
        final WFSHost duinoordBagWFS = new WFSHost("DUINOORD BAG WFS",
                "https://duinoord.xs4all.nl/geoserver/wfs?VERSION=2.0.0", 0,
                1000, 60000);

        Hosts() {
        }
    }

    private class GtFeatureSources {
        // PDOK
        final GtFeatureSource pand;
        final GtFeatureSource standplaats;
        final GtFeatureSource ligplaats;
        final GtFeatureSource verblijfsobject;
        // Duinoord
        //        GtFeatureSource duinoordAdres;
        final GtFeatureSource duinoordAddressMissing;
        final GtFeatureSource duinoordAddressWithdrawn;

        public GtFeatureSources(Hosts hosts) {
            WFSHost pdok = hosts.pdokBagWFS;
            this.pand = new GtFeatureSource(pdok, "bag:pand", "identificatie");
            this.ligplaats = new GtFeatureSource(pdok, "bag:ligplaats", "identificatie");
            this.standplaats = new GtFeatureSource(pdok, "bag:standplaats", "identificatie");
            this.verblijfsobject = new GtFeatureSource(pdok, "bag:verblijfsobject", "identificatie");
            WFSHost duinoord = hosts.duinoordBagWFS;
            //  new GtFeatureSource(duinoord, "bag:All_Addresses", "nummeraanduiding");
            this.duinoordAddressMissing = new GtFeatureSource(duinoord, "bag:Address_Missing", "nummeraanduiding");
            this.duinoordAddressWithdrawn = new GtFeatureSource(duinoord, "bag:Address_Withdrawn", "nummeraanduiding");
        }

        public Collection<GtFeatureSource> getAll() {
            return Arrays.asList(pand, standplaats, ligplaats,
                    verblijfsobject,duinoordAddressMissing, duinoordAddressWithdrawn);
        }
    }

    private class DataSources {
        final GtDataSource pand;
        final GtDataSource standplaats;
        final GtDataSource ligplaats;
        final GtDataSource verblijfsobject;
        //        GtDataSource duinoordAdres;
        final GtDataSource duinoordAddressMissing;
        final GtDataSource duinoordAddressWithdrawn;

        public DataSources(GtFeatureSources featureSources) {
            this.pand = createPandDataSource(featureSources.pand);
            this.ligplaats = createLigplaatsDataSource(
                    featureSources.ligplaats);
            this.standplaats = createStandplaatsDataSource(
                    featureSources.standplaats);
            this.verblijfsobject = createVerblijfsobjectDataSource(
                    featureSources.verblijfsobject);
            //        dataSources.duinoordAdres = createDuinoordAdresDataSource(
            //                featureSources.duinoordAdres);
            this.duinoordAddressMissing = createDuinoordAddressMissingDataSource(
                    featureSources.duinoordAddressMissing);
            this.duinoordAddressWithdrawn = createDuinoordAddressWithdrawnDataSource(
                    featureSources.duinoordAddressWithdrawn);
        }
    }

    private class FeatureDownloaders {
        final FeatureDownloader pand;
        final FeatureDownloader standplaats;
        final FeatureDownloader ligplaats;
        final FeatureDownloader verblijfsobject;
        final FeatureDownloader duinoordAddressMissing;
        final FeatureDownloader duinoordAddressWithdrawn;

        public FeatureDownloaders(DataSources dataSources, Parsers parsers) {
            this.pand = new GtDownloader(dataSources.pand, parsers.pand);
            this.ligplaats = new GtDownloader(dataSources.ligplaats, parsers.ligplaats);
            this.standplaats = new GtDownloader(dataSources.standplaats, parsers.standplaats);
            this.verblijfsobject = new GtDownloader(
                    dataSources.verblijfsobject, parsers.verblijfsobject);
            //        downloaders.duinoordAddress = new GtDownloader(
            //                dataSources.duinoordAdres, crsUtil, parsers.duinoordAddress);
            this.duinoordAddressMissing = new GtDownloader(
                    dataSources.duinoordAddressMissing, parsers.duinoordAddressMissing);
            this.duinoordAddressWithdrawn = new GtDownloader(
                    dataSources.duinoordAddressWithdrawn, parsers.duinoordAddressWithdrawn);
        }

        List<FeatureDownloader> all() {
            return Arrays.asList(pand, standplaats, ligplaats, verblijfsobject, duinoordAddressMissing, duinoordAddressWithdrawn);
        }
    }

    static class LayerDownloaders {
        final OsmLayerDownloader osm;
        final OpenDataLayerDownloader od;

        public LayerDownloaders(OsmLayerDownloader osm,
                OpenDataLayerDownloader od) {
            super();
            this.osm = osm;
            this.od = od;
        }
    }

    static class PrimitiveBuilders  {
        EntityPrimitiveBuilder<OdBuilding> building;
        EntityPrimitiveBuilder<OdAddressNode> addressNode;
    }
}
