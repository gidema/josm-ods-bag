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
//import org.openstreetmap.josm.plugins.ods.builtenvironment.actions.AlignBuildingsAction;
import org.openstreetmap.josm.plugins.ods.builtenvironment.actions.RemoveAssociatedStreetsAction;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.tools.I18n;

public class BagImportModule extends OdsModule {
    // Boundary of the Netherlands
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
//    private final OdsDownloader odsDownloader;
    private final MainDownloader mainDownloader;
    private BagPrimitiveBuilder bagPrimitiveBuilder = new BagPrimitiveBuilder(this);
    private GeoUtil geoUtil = new GeoUtil();
    private CRSUtil crsUtil = new CRSUtilProj4j();

    public BagImportModule(OdsModulePlugin plugin) {
        super(plugin, new ExternalDataLayer("BAG ODS"), 
            new InternalDataLayer("BAG OSM"));
        this.mainDownloader = new BagDownloader(this);
//        this.odsDownloader = new BagWfsDownloader(this);
        addAction(new OdsDownloadAction(this));
        addAction(new RemoveAssociatedStreetsAction(this));
//        actions.add(new AlignBuildingsAction(this));
//        actions.add(new RemoveShortSegmentsAction(this));
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

//    @Override
//    public OdsDownloader getDownloader() {
//        return odsDownloader;
//    }

    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @Override
    public void activate() {
        if (!checkUser()) {
            int answer = JOptionPane.showConfirmDialog(Main.parent, 
                 "Je gebruikersnaam eindigt niet op _BAG en is daarom niet geschikt " +
                 "voor de BAG import.\nWeet je zeker dat je door wilt gaan?",
                I18n.tr("Invalid user"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (answer == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        super.activate();
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
