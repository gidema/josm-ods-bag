package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public interface OsmBagStaticCaravanPlot extends OsmAddressableObject {

    public Long getBagId();

    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> mapping);
}
