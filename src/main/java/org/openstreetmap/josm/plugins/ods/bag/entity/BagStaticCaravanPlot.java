package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagStaticCaravanPlot extends OdEntity, NlAddressable {
    public Long getStandplaatsId();

    public PlotStatus getStatus();
    
    public void setMatch(OdMatch<BagStaticCaravanPlot> match);
}
