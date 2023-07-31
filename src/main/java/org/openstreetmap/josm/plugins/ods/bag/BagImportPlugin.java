package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.data.validation.OsmValidator;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.bag.validation.BAGReferences;

public class BagImportPlugin extends OdsModulePlugin {
    private final static BagPreferences bagPreferences = new BagPreferences(); 
    
    public BagImportPlugin(PluginInformation info) throws Exception {
        super(info, new BagImportModule());
        OsmValidator.addTest(BAGReferences.class);
        
    }

    public static BagPreferences getPreferences() {
        return bagPreferences;
    }
}
