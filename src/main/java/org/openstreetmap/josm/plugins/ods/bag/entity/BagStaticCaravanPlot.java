package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagStaticCaravanPlot extends BagAddressableObject {

    public PlotStatus getStatus();
    
    public void setMatch(OdMatch<BagStaticCaravanPlot> match);
}
