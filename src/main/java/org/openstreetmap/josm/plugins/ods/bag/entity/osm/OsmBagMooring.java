package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;

public interface OsmBagMooring extends OsmAddressableObject {
    public Long getMooringId();

    /**
     * Check is the full area of this building has been loaded. This is true if
     * the building is completely covered by the downloaded area.
     *
     * @return
     */

    public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> mapping);
}
