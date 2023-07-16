package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.ParameterType;
import org.openstreetmap.josm.plugins.ods.bag.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagLanduseStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBagMooringStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNode2BuildingPairStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnit2BuildingPairStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnitStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringParcelStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanParcelStore;
import org.openstreetmap.josm.plugins.ods.bag.factories.BagBuildingFactory;
import org.openstreetmap.josm.plugins.ods.bag.factories.BagBuildingUnitFactory;
import org.openstreetmap.josm.plugins.ods.bag.factories.BagMooringParcelFactory;
import org.openstreetmap.josm.plugins.ods.bag.factories.BagStaticCaravanParcelFactory;
import org.openstreetmap.josm.plugins.ods.bag.factories.BuildingUnitToBuildingRelationFactory;
import org.openstreetmap.josm.plugins.ods.bag.gui.UpdateBuildingGeometryAction;
import org.openstreetmap.josm.plugins.ods.bag.importing.BagImportContext;
import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.bag.match.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.bag.match.MooringMatcher;
import org.openstreetmap.josm.plugins.ods.bag.match.OsmAddressNodeToBuildingConnector;
import org.openstreetmap.josm.plugins.ods.bag.match.StaticCaravanSiteMatcher;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodeEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingEntityPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagMooringParcelPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmLanduseBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmMooringBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagStaticCaravanParcelPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.bag.process.BuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.bag.process.DistributeAddressNodes;
import org.openstreetmap.josm.plugins.ods.bag.relate.BagBuildingUnit2BuildingConnector;
import org.openstreetmap.josm.plugins.ods.bag.tools4osm.BagTools4Osm;
import org.openstreetmap.josm.plugins.ods.context.ContextJobList;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactories;
import org.openstreetmap.josm.plugins.ods.entities.impl.OdEntityFactoriesImpl;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilders;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.MenuActions;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsResetAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsUpdateAction;
import org.openstreetmap.josm.plugins.ods.io.OsmHost;
import org.openstreetmap.josm.plugins.ods.io.OverpassHost;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.matching.Matchers;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImportContext;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureSource;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureSourceBuilder;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureSources;
import org.openstreetmap.josm.plugins.ods.wfs.WfsHost;
import org.openstreetmap.josm.plugins.ods.wfs.WfsHostBuilder;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class BagImportModule extends OdsModule {
    public final static ParameterType<Double> BuildingAlignmentTolerance = new ParameterType<>(
            Double.class);

    // Boundary of the Netherlands
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583,
            7.245);

    public BagImportModule() {
    }

    @Override
    public void initialize() throws Exception {
        super.initialize();
    }

    @Override
    protected OsmLayerManager createOsmLayerManager() {
        return new OsmLayerManager("BAG OSM");
    }

    @Override
    protected OdLayerManager createOpenDataLayerManager() {
        return new OdLayerManager("BAG ODS");
    }

    @Override
    public String getName() {
        return "BAG";
    }

    @Override
    public String getDescription() {
        return I18n.tr(
                "ODS module to import buildings and addresses in the Netherlands");
    }

    @Override
    public Bounds getBounds() {
        return BOUNDS;
    }

    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean activate() {
        if (false && !checkUser()) { // Disabled, but kept the code in case we
                                     // need it
            int answer = JOptionPane.showConfirmDialog(
                    MainApplication.getMainFrame(),
                    "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt "
                            + "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                    I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return super.activate();
    }

    @Override
    protected void configureContext() {
        OdsContext context = getContext();
        context.setParameter(BuildingAlignmentTolerance, 0.1d);

        context.register(OdsModule.class, this);

        context.register(GeoUtil.class, GeoUtil.getInstance());
        context.register(CRSUtil.class, new CRSUtilProj4j());

        registerEntityStores(context);
        
        // Register WFS Hosts and featureSources
        createHosts(context);
        createWfsFeatureSources(context);

        createOdEntityFactories(context);
        
        // Create Osm entity builder
        createOsmEntityBuilders(context);
        
        context.register(OdsImportContext.class, new BagImportContext());

        context.register(OsmHost.class, new OverpassHost());

        context.register(Matchers.class, createMatchers(context));

        // Register post-download jobs
        ContextJobList  postDownloadJobs = ContextJobList.of(
            new BagBuildingUnit2BuildingConnector(),
            new BuildingCompletenessEnricher(),
            new DistributeAddressNodes(),
            new BuildingTypeEnricher(),
            new BagPrimitiveBuilder(List.of(
                new BagBuildingEntityPrimitiveBuilder(),
                new BagMooringParcelPrimitiveBuilder(),
                new BagStaticCaravanParcelPrimitiveBuilder(),
                new BagAddressNodeEntityPrimitiveBuilder())),
//            new BagDemolishedBuildingPrimitiveBuilder())),
            new OsmAddressNodeToBuildingConnector());
        context.register(postDownloadJobs, "postDownloadJobs");

        getContext().register(createMenuActions(context));
    }

    private static void registerEntityStores(OdsContext context) {
        // Register BAG Full load entity stores
        context.register(new BagBuildingStore());
        context.register(new BagBuildingUnitStore());
        context.register(new BagMooringParcelStore());
        context.register(new BagStaticCaravanParcelStore());
        context.register(new BagAddressNodeStore());
        context.register(new BagBuildingUnit2BuildingPairStore());
        context.register(new BagAddressNode2BuildingPairStore());
        
        // Register OSM entity Stores
        context.register(new OsmBuildingStore());
        context.register(new OsmAddressNodeStore());
        context.register(new OsmBagMooringStore());
        context.register(new OsmBagLanduseStore());

    }

    private static void createOdEntityFactories(OdsContext context) {
        OdEntityFactories factories = new OdEntityFactoriesImpl(
            new BagBuildingFactory(context),
            new BagBuildingUnitFactory(context),
            new BagMooringParcelFactory(context),
            new BagStaticCaravanParcelFactory(context),
            new BuildingUnitToBuildingRelationFactory(context)
//            new BagAddressNodeToBuildingRelationFactory(context)
        );
        context.register(OdEntityFactories.class, factories);
    }

    private static void createOsmEntityBuilders(OdsContext context) {
        OsmEntityBuilders entityBuilders = new OsmEntityBuilders(
                new BagOsmBuildingBuilder(context),
                new BagOsmAddressNodeBuilder(context),
                new BagOsmMooringBuilder(context),
                new BagOsmLanduseBuilder(context));
        context.register(entityBuilders);
    }
    private static Matchers createMatchers(OdsContext context) {
        return new Matchers(
                new BuildingMatcher(context),
                new AddressNodeMatcher(context),
                new MooringMatcher(context),
                new StaticCaravanSiteMatcher(context));
    }

    private static MenuActions createMenuActions(OdsContext context) {
        MenuActions actions = new MenuActions();
        actions.addAction(new OdsDownloadAction(context));
        // addAction(new RemoveAssociatedStreetsAction(this));
        // addAction(new OdsImportAction(this));
        actions.addAction(new OdsUpdateAction(context));
        actions.addAction(new OdsResetAction(context));
        actions.addAction(new UpdateBuildingGeometryAction(context));
        return actions;
    }

    private static void createHosts(OdsContext context) {
        // Create the default BAG Host
        WfsHostBuilder builder = new WfsHostBuilder(BagPdok.HOST_NAME, BagPdok.URL,
                   BagPdok.NS_BAG, "bag", "geom", 28992L, 10000);
            builder.setPageSize(500);
            // The BAG WFS servers support HTTP POST and simple XML Filters
            // However, the symantics are hard to grasp. For now, we have to deal with HTTP Get
            builder.setHttpMethod("GET");
            builder.setFesFilterCapable(false);
        WfsHost host = builder.build();
        context.register(WfsHost.class, host, host.getName());
        
        builder = new WfsHostBuilder(BagTools4Osm.HOST_NAME, BagTools4Osm.URL,
                BagTools4Osm.NS_TOOSL4OSM, "bag", "", 28992L, 10000);
            builder.setPageSize(500);
            builder.setHttpMethod("GET");
            builder.setFesFilterCapable(false);
         host = builder.build();
         context.register(WfsHost.class, host, host.getName());
    }
    
    private static void createWfsFeatureSources(OdsContext context) {
        WfsFeatureSources featureSources = new WfsFeatureSources(createBuildingFeatureSource(context),
                createBuildingUnitFeatureSource(context),
                createMooringParcelFeatureSource(context),
                createStaticCaravanParcelFeatureSource(context),
                createMissingSecondaryAddressFeatureSource(context));
        context.register(featureSources);
//        featureSources = new WfsFeatureSources(
////                createMissingSecondaryAddressFeatureSource(context),
//                createMissingAddressFeatureSource(context),
//                createMissingBuildingFeatureSource(context),
//                createDemolishedBuildingFeatureSource(context),
//                createWithdrawnAddressFeatureSource(context),
//                createChangedBuildingGeometryFeatureSource(context));
//        context.register(featureSources, "Update");
    }

    private static WfsFeatureSource createBuildingUnitFeatureSource(OdsContext context) {
        WfsHost wfsHost = context.getComponent(WfsHost.class, BagPdok.HOST_NAME);
        WfsFeatureSourceBuilder builder = new WfsFeatureSourceBuilder(wfsHost, "verblijfsobject");
        builder.setProperties("identificatie", "oppervlakte", "status", "gebruiksdoel",
                "openbare_ruimte", "huisnummer", "huisletter", "toevoeging", "postcode", "woonplaats",
                "geometrie", "pandidentificatie");
        builder.setSortBy("identificatie", "pandidentificatie");
        return builder.build();
    }
    
    private static WfsFeatureSource createBuildingFeatureSource(OdsContext context) {
        WfsHost wfsHost = context.getComponent(WfsHost.class, BagPdok.HOST_NAME);
        WfsFeatureSourceBuilder builder = new WfsFeatureSourceBuilder(wfsHost, "pand");
        builder.setProperties("identificatie", "bouwjaar", "status", "aantal_verblijfsobjecten",
                "geometrie");
        builder.setPageSize(500);
        builder.setSortBy("identificatie");
        return builder.build();
    }

    private static WfsFeatureSource createMooringParcelFeatureSource(OdsContext context) {
        WfsHost wfsHost = context.getComponent(WfsHost.class, BagPdok.HOST_NAME);
        WfsFeatureSourceBuilder builder = new WfsFeatureSourceBuilder(wfsHost, "ligplaats");
        builder.setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        builder.setPageSize(500);
        builder.setSortBy("identificatie");
        return builder.build();
    }

    private static WfsFeatureSource createStaticCaravanParcelFeatureSource(OdsContext context)  {
        WfsHost wfsHost = context.getComponent(WfsHost.class, BagPdok.HOST_NAME);
        WfsFeatureSourceBuilder builder = new WfsFeatureSourceBuilder(wfsHost, "standplaats");
        builder.setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geometrie");
        builder.setPageSize(500);
        builder.setSortBy("identificatie");
        return builder.build();
    }

    private static WfsFeatureSource createMissingSecondaryAddressFeatureSource(OdsContext context) {
        WfsHost wfsHost = context.getComponent(WfsHost.class, BagTools4Osm.HOST_NAME);
        WfsFeatureSourceBuilder builder = new WfsFeatureSourceBuilder(wfsHost, "SecondaryAddress_Missing");
        builder.setProperties("identificatie", "status", "openbare_ruimte", "huisnummer",
                "huisletter", "toevoeging", "postcode", "woonplaats", "geopunt", "pandidentificatie");
        builder.setSortBy("identificatie", "pandidentificatie");
        builder.setGeometryProperty("geopunt");
        return builder.build();
    }

    private static boolean checkUser() {
        try {
            final UserInfo userInfo = new OsmServerUserInfoReader()
                    .fetchUserInfo(NullProgressMonitor.INSTANCE);
            String user = userInfo.getDisplayName();
            String suffix = "_BAG";
            return user.endsWith(suffix);
        } catch (OsmTransferException e1) {
            Logging.warn(
                    tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }
}
