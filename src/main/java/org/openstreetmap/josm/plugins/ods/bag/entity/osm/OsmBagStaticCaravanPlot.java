package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.matching.OsmMatch;

public interface OsmBagStaticCaravanPlot extends OsmAddressableObject {

    public Long getBagId();

    public void setMatch(OsmMatch<OsmBagStaticCaravanPlot> bagStaticCaravanPlotMatch);
}
