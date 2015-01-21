package org.openstreetmap.josm.plugins.ods.bag;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.UserInfo;
import org.openstreetmap.josm.gui.progress.NullProgressMonitor;
import org.openstreetmap.josm.io.OsmServerUserInfoReader;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.builtenvironment.actions.AlignBuildingsAction;
import org.openstreetmap.josm.plugins.ods.builtenvironment.actions.RemoveAssociatedStreetsAction;
import org.openstreetmap.josm.plugins.ods.builtenvironment.actions.RemoveShortSegmentsAction;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;
import org.openstreetmap.josm.plugins.ods.gui.OdsDownloadAction;
import org.openstreetmap.josm.tools.I18n;

public class BagImportModule extends OdsModule {
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
    private List<OdsAction> actions = new LinkedList<>();

    public BagImportModule(OdsModulePlugin plugin, org.openstreetmap.josm.plugins.ods.io.OdsDownloader downloader, 
            ExternalDataLayer externalDataLayer, InternalDataLayer internalDataLayer) {
        super(plugin, downloader, externalDataLayer, internalDataLayer);
        actions.add(new OdsDownloadAction(this));
        actions.add(new RemoveAssociatedStreetsAction(this));
        actions.add(new AlignBuildingsAction(this));
        actions.add(new RemoveShortSegmentsAction(this));
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
    public boolean usePolygonFile() {
        return true;
    }

    @Override
    public List<OdsAction> getActions() {
        return actions;
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
        } catch (OsmTransferException e1) {
            Main.warn(tr("Failed to retrieve OSM user details from the server."));
            return false;
        }
    }
}
