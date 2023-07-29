package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;

public interface BagMooringPlot extends OdEntity, NlAddressable {

    public Long getLigplaatsId();
    
    public PlotStatus getStatus();
    
    public void setMatch(OdMatch<BagMooringPlot> match);
}
