package org.openstreetmap.josm.plugins.ods.bag.actions;

import java.awt.event.ActionEvent;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNodeDistributor;

public class FixOverlappingNodesAction extends JosmAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    
    public FixOverlappingNodesAction() {
        super("Fix overlapping nodes", null, "Fix overlapping nodes", null, false);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        OdsWorkingSet workingSet =ODS.getModule().getWorkingSet();
        AddressNodeDistributor distributor = new AddressNodeDistributor();
        distributor.analyze(workingSet.getInternalDataLayer(), null);
        Main.map.repaint();
    }

}
