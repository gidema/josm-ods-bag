package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.Context;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.OdBuildingTypeEnricher;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingStatusAnalyzer;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.StartYearAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.BuildingCompletenessEnricher;
import org.openstreetmap.josm.plugins.ods.entities.enrichment.OdAddressNodesDistributer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntitiesBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsResetAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsUpdateAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.matching.OdAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.matching.OsmAddressNodeToBuildingMatcher;
import org.openstreetmap.josm.plugins.ods.matching.OsmAnalyzer;
import org.openstreetmap.josm.plugins.ods.matching.OsmEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsImporter;
import org.openstreetmap.josm.plugins.ods.matching.update.OdsUpdater;
import org.openstreetmap.josm.plugins.ods.osm.OsmBuildingAligner;
import org.openstreetmap.josm.plugins.ods.osm.OsmNeighbourFinder;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.Logging;

public class BagImportModule extends OdsModule {
    private final Context context = new Context();
    // Boundary of the Netherlands
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
    private MainDownloader mainDownloader;
    private final GeoUtil geoUtil = new GeoUtil();
    private final CRSUtil crsUtil = new CRSUtilProj4j();

    public BagImportModule() {
        super();
    }

    @Override
    public void initialize() throws Exception {
        super.initialize();
        context.put(CRSUtil.class, crsUtil);
        context.put(geoUtil);
        context.put(osmLayerManager);
        context.put(odLayerManager);
        OdAddressNodeStore odAddressNodeStore = context.put(new OdAddressNodeStore());
        OdBuildingStore odBuildingStore = context.put(new OdBuildingStore());
        OsmAddressNodeStore osmAddressNodeStore = context.put(new OsmAddressNodeStore());
        OsmBuildingStore osmBuildingStore = context.put(new OsmBuildingStore());

        OdAddressNodeToBuildingMatcher odAddressNodeToBuildingMatcher = context.put(new OdAddressNodeToBuildingMatcher(odBuildingStore, odAddressNodeStore));
        BuildingCompletenessEnricher buildingCompletenessEnricher = context.put(new BuildingCompletenessEnricher(odBuildingStore));
        OsmAddressNodeToBuildingMatcher osmNodeToBuildingMatcher = context.put(new OsmAddressNodeToBuildingMatcher(osmBuildingStore));
        OdAddressNodesDistributer addressNodesDistrubuter = context.put(new OdAddressNodesDistributer(odBuildingStore, geoUtil));
        OdBuildingTypeEnricher buildingTypeEnricher = context.put(new OdBuildingTypeEnricher(odBuildingStore));
        List<OsmEntityBuilder<?>> entityBuilders = new ArrayList<>(2);
        entityBuilders.add(new BagOsmBuildingBuilder(osmLayerManager, osmBuildingStore, geoUtil));
        entityBuilders.add(new BagOsmAddressNodeBuilder(osmLayerManager, osmAddressNodeStore, geoUtil));
        OsmEntitiesBuilder entitiesBuilder = context.put(new OsmEntitiesBuilder(osmAddressNodeStore, osmNodeToBuildingMatcher, entityBuilders, osmLayerManager));
        OsmBuildingAligner osmBuildingAligner = new OsmBuildingAligner(osmBuildingStore);
        OsmNeighbourFinder osmNeighbourFinder = new OsmNeighbourFinder(osmBuildingAligner, getTolerance());

        OsmAnalyzer osmAnalyzer = context.put(createOsmAnalyzer(osmBuildingStore));
        this.mainDownloader = context.put(new BagDownloader(context));
        OdsImporter importer = new OdsImporter(osmNeighbourFinder, odLayerManager, osmLayerManager, entitiesBuilder);
        OdsUpdater updater = new OdsUpdater(osmLayerManager);
        mainDownloader.initialize();
        addAction(new OdsDownloadAction(odLayerManager, mainDownloader, getName()));
        //        addAction(new RemoveAssociatedStreetsAction(this));
        //        addAction(new OdsImportAction(this));
        addAction(new OdsUpdateAction(osmLayerManager, odLayerManager, importer, updater));
        addAction(new OdsResetAction(this));
    }

    @Override
    public String getName() {
        return "BAG";
    }

    @Override
    public String getDescription() {
        return I18n.tr("ODS module to import buildings and addresses in the Netherlands");
    }

    @Override
    public Bounds getBounds() {
        return BOUNDS;
    }

    @Override
    public MainDownloader getDownloader() {
        return mainDownloader;
    }

    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @SuppressWarnings("unused")
    @Override
    public boolean activate() {
        if (false && !checkUser()) { // Disabled, but kept the code in case we need it
            int answer = JOptionPane.showConfirmDialog(Main.parent,
                    "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
                            "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                            I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return super.activate();
    }

    @Override
    public Double getTolerance() {
        return 1e-5;
    }

    private static boolean checkUser() {
        try {
            final UserInfo userInfo = new OsmServerUserInfoReader().fetchUserInfo(NullProgressMonitor.INSTANCE);
            String user = userInfo.getDisplayName();
            String suffix = "_BAG";
            return user.endsWith(suffix);
        } catch (OsmTransferException e1) {
            Logging.warn(tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }

    private static OsmAnalyzer createOsmAnalyzer(OsmBuildingStore osmBuildingStore) {
        OsmEntityAnalyzer<OsmBuilding> buildingAnalyzer = new OsmEntityAnalyzer<>(OsmBuilding.class, osmBuildingStore,
                new StartYearAnalyzer(), new BuildingStatusAnalyzer());
        return new OsmAnalyzer(Arrays.asList(
                buildingAnalyzer));
    }

    @Override
    protected OdLayerManager createOdLayerManager() {
        return new OdLayerManager("BAG ODS");
    }

    @Override
    protected OsmLayerManager createOsmLayerManager() {
        return new OsmLayerManager("BAG OSM");
    }
}
