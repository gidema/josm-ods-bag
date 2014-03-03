package org.openstreetmap.josm.plugins.ods.bag.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.AddCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.PleaseWaitRunnable;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.io.OsmTransferException;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.tools.I18n;
import org.xml.sax.SAXException;

public class DisconnectPoiFromBuildingAction extends JosmAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DisconnectPoiFromBuildingAction() {
        super(I18n.tr("Disconnect poi's"), null, I18n
                .tr("Disconnect poi's from buildings."), null, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OdsWorkingSet workingSet = ODS.getModule().getWorkingSet();
        if (workingSet.getInternalDataLayer() == null) {
            new Notification(I18n.tr("The OSM datalayer is missing")).show();
            return;
        }
        Main.worker.execute(new Task());
    }

    class Task extends PleaseWaitRunnable {
        private OsmDataLayer dataLayer;
        
        public Task() {
            super(I18n.tr("Please wait"));
        }

        @Override
        protected void cancel() {
            // TODO Auto-generated method stub

        }

        @Override
        protected void realRun() throws SAXException, IOException,
                OsmTransferException {
            OdsWorkingSet workingSet = ODS.getModule().getWorkingSet();
            dataLayer = workingSet.getInternalDataLayer()
                    .getOsmDataLayer();
            for (Way way : dataLayer.data.getWays()) {
                if (way.hasKey("building")) {
                    process(way);
                }
            }
            for (Relation relation : dataLayer.data.getRelations()) {
                for (OsmPrimitive primitive : relation.getMemberPrimitives()) {
                    if (primitive.getType().equals(OsmPrimitiveType.WAY)) {
                        process((Way)primitive);
                    }
                }
            }
            Main.map.repaint();
        }

        private void process(Way way) {
            for (int i=1; i<way.getNodesCount(); i++) {
                Node node = way.getNode(i);
                if (node.hasKeys()) {
                    Node newNode = new Node(node, true);
                    node.setKeys(new HashMap<String, String>());
                    Command cmd = new AddCommand(dataLayer, newNode);
                    Main.main.undoRedo.add(cmd);
                }
            }
        }

        @Override
        protected void finish() {
            // TODO Auto-generated method stub

        }

    }
}
