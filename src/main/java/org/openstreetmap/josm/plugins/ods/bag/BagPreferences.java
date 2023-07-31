package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.PreferencesUtils;
import org.openstreetmap.josm.spi.preferences.PreferenceChangeEvent;
import org.openstreetmap.josm.spi.preferences.PreferenceChangedListener;
import org.openstreetmap.josm.spi.preferences.Setting;

public class BagPreferences implements PreferenceChangedListener {
    public static final String BAG_PREFERENCES = "ods-bag";
    public static final String STATIC_CARAVAN_ADDRESS_NODE = "static_caravan.address_node";
    public static final String STATIC_CARAVAN_LANDUSE = "static_caravan.landuse";
    
    // Decide whether to render addresses of static caravan as separate nodes
    // as opposed to adding the address to the static caravan plot geometry   
    private boolean staticCaravanAddressNode = true;

    // Decide whether to render static caravan with a 'landuse=static_caravan' tag
    private boolean staticCaravanLanduse = true;

    public BagPreferences() {
        super();
        Preferences pref = Preferences.main();
        pref.addPreferenceChangeListener(this);
        staticCaravanAddressNode = PreferencesUtils.getBoolean(pref, BAG_PREFERENCES, STATIC_CARAVAN_ADDRESS_NODE, true);
        staticCaravanLanduse = PreferencesUtils.getBoolean(pref, BAG_PREFERENCES, STATIC_CARAVAN_LANDUSE, true);
    }

    @Override
    public void preferenceChanged(PreferenceChangeEvent e) {
        Setting<?> setting = e.getNewValue();
        String value = setting.getValue().toString();
        String[] keyParts = e.getKey().split("\\.", 2);
        if (keyParts.length == 2 && BAG_PREFERENCES.equals(keyParts[0])) {
        switch (keyParts[1]) {
            case STATIC_CARAVAN_ADDRESS_NODE: {
                staticCaravanAddressNode = Boolean.valueOf(value);
                return;
            }
            case STATIC_CARAVAN_LANDUSE: {
                staticCaravanLanduse = Boolean.valueOf(value);
                return;
            }
           default:
                break;
            }
        }
    }

    public boolean isStaticCaravanAddressNode() {
        return staticCaravanAddressNode;
    }

    public boolean isStaticCaravanLanduse() {
        return staticCaravanLanduse;
    }
}
