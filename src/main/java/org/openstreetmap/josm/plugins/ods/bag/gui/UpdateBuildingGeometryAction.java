package org.openstreetmap.josm.plugins.ods.bag.gui;

import java.awt.event.ActionEvent;
import java.util.List;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.Layer;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.ods.bag.osm.update.BuildingUpdater;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.gui.OdsAction;

public class UpdateBuildingGeometryAction extends OdsAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UpdateBuildingGeometryAction(OdsContext context) {
        super(context, "Update building Geometry", (String)null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Layer layer = MainApplication.getLayerManager().getActiveLayer();
        OdLayerManager odLayerManager = getContext().getComponent(OdLayerManager.class);
        OsmLayerManager osmLayerManager = getContext().getComponent(OsmLayerManager.class);
        // This action should only occur when the OpenData layer is active
        assert (odLayerManager != null);
        
        BuildingUpdater updater = new BuildingUpdater(getContext());
        List<Command> cmds = updater.updateGeometries((OsmDataLayer)layer);
        if (cmds.isEmpty()) return;
        UndoRedoHandler.getInstance().add(new SequenceCommand(getValue(NAME).toString(), cmds));

        odLayerManager.getOsmDataLayer().getDataSet().clearSelection();
        MainApplication.getLayerManager().setActiveLayer(osmLayerManager.getOsmDataLayer());
    }

    @Override
    public void activeLayerChange(Layer oldLayer, Layer newLayer) {
        if (newLayer != null) {
            this.setEnabled(newLayer.equals(getContext().getComponent(OdLayerManager.class).getOsmDataLayer()));
        }
        else setEnabled(false);
    }    
}
