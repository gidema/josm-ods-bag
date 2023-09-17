package org.openstreetmap.josm.plugins.ods.bag.entity;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public interface BagMooringPlot extends BagAddressableObject {

    public PlotStatus getStatus();
    
    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> match);
}
