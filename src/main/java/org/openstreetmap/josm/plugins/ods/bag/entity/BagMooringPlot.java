package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagMooringPlot extends BagAddressableObject {

    public PlotStatus getStatus();
    
    public void setMatch(OdMatch<BagMooringPlot> match);
}
