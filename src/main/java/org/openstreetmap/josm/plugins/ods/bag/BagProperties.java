package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.data.preferences.IntegerProperty;
import org.openstreetmap.josm.data.preferences.StringProperty;
import org.openstreetmap.josm.tools.I18n;

public final class BagProperties {

    public static final StringProperty WFS_URL = new StringProperty("nl.bag.pdok.bag.wfs.url", "https://geodata.nationaalgeoregister.nl/bag/wfs/v1_1");
    public static final IntegerProperty WFS_INIT_TIMEOUT = new IntegerProperty("nl.bag.pdok.bag.wfs.init_timeout", 1000);
    public static final IntegerProperty WFS_DATA_TIMEOUT = new IntegerProperty("nl.bag.pdok.bag.wfs.data_timeout", 10000);

  private BagProperties() {
    // Private constructor to avoid instantiation
  }
}
