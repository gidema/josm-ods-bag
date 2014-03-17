package org.openstreetmap.josm.plugins.ods.bag.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.command.ChangeCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
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

/**
 * Earlier version of the ODS-BAG plug-in have introduced tags that
 * are no longer used, or used differently.
 * This action removes the older tags or updates then to their new
 * equivalent. 
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class UpdateBagTagsAction extends JosmAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UpdateBagTagsAction() {
        super(I18n.tr("Update BAG tags"), null, I18n
                .tr("Update older BAG tags."), null, false);
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
                if (way.hasKey("building") || way.hasKey("building:part")) {
                    updateBuilding(way);
                }
            }
            for (Relation relation : dataLayer.data.getRelations()) {
                if (relation.hasKey("building") || relation.hasKey("building:part")) {
                    updateBuilding(relation);
                }
            }
            Main.map.repaint();
        }

        private void updateBuilding(OsmPrimitive primitive) {
            Map<String, String> oldKeys = primitive.getKeys();
            Map<String, String> keys = primitive.getKeys();
            boolean updated = false;
            for (Entry<String, String> entry : oldKeys.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                switch (key) {
                case "ref:bagid":
                    keys.remove(key);
                    keys.put("ref:bag", value);
                    updated = true;
                    break;
                case "bag:status":
                case "bag:versie":
                case "bag:begindatum":
                    keys.remove(key);
                    updated = true;
                    break;
                case "bag:bouwjaar":
                    keys.remove(key);
                    keys.put("start_date", value);
                    break;
                default:
                    break;
                }
                if (updated) {
                    Command cmd = null;
                    switch (primitive.getType()) {
                    case WAY: 
                        Way newWay = new Way((Way)primitive);
                        newWay.setKeys(keys);
                        cmd = new ChangeCommand(primitive, newWay);
                        break;
                    case RELATION:
                        Relation newRelation = new Relation((Relation)primitive);
                        newRelation.setKeys(keys);
                        cmd = new ChangeCommand(primitive, newRelation);
                        break;
                     default:
                    }
                    if (cmd != null) {
                        Main.main.undoRedo.add(cmd);
                    }
                }
            }
        }

        @Override
        protected void finish() {
            // TODO Auto-generated method stub

        }

    }
}
