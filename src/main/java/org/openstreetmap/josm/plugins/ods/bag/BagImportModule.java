package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.tools.I18n.tr;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.AddressNodeEntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.BuildingEntityType;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.opendata.OpenDataBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.actual.impl.osm.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsResetAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsUpdateAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.tools.I18n;

public class BagImportModule extends OdsModule {
    // Boundary of the Netherlands
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
//    private final OdsDownloader odsDownloader;
    private final MainDownloader mainDownloader;
    private GeoUtil geoUtil = new GeoUtil();
    private CRSUtil crsUtil = new CRSUtilProj4j();

    public BagImportModule(OdsModulePlugin plugin) {
        super(plugin);
        this.mainDownloader = new BagDownloader(this);
        addEntityType(BuildingEntityType.getInstance());
        addEntityType(AddressNodeEntityType.getInstance());
        addOsmEntityBuilder(new BagOsmBuildingBuilder(this));
        addOsmEntityBuilder(new BagOsmAddressNodeBuilder(this));
        addAction(new OdsDownloadAction(this));
//        addAction(new RemoveAssociatedStreetsAction(this));
//        addAction(new OdsImportAction(this));
        addAction(new OdsUpdateAction(this));
        addAction(new OdsResetAction(this));
//        actions.add(new AlignBuildingsAction(this));
//        actions.add(new RemoveShortSegmentsAction(this));
    }

    @Override
    protected OsmLayerManager createOsmLayerManager() {
        OsmLayerManager manager = new OsmLayerManager(this, "BAG OSM");
        manager.addEntityStore(Building.class, new OsmBuildingStore());
        manager.addEntityStore(AddressNode.class, new OsmAddressNodeStore());
        return manager;
    }

    @Override
    protected OpenDataLayerManager createOpenDataLayerManager() {
        OpenDataLayerManager manager = new OpenDataLayerManager("BAG ODS");
        manager.addEntityStore(Building.class, new OpenDataBuildingStore());
        manager.addEntityStore(AddressNode.class, new OpenDataAddressNodeStore());
        return manager;
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
    public GeoUtil getGeoUtil() {
        return geoUtil;
    }

    @Override
    public CRSUtil getCrsUtil() {
        return crsUtil;
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
    
    private static boolean checkUser() {
        try {
            final UserInfo userInfo = new OsmServerUserInfoReader().fetchUserInfo(NullProgressMonitor.INSTANCE);
            String user = userInfo.getDisplayName();
            String suffix = "_BAG";
            return user.endsWith(suffix);
        } catch (@SuppressWarnings("unused") OsmTransferException e1) {
            Main.warn(tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }
}
