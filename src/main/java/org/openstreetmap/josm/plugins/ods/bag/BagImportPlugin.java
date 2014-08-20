package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfig;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;

public class BagImportPlugin extends OdsModulePlugin {
    private OdsModuleConfig moduleConfig;
    
    public BagImportPlugin(PluginInformation info) throws Exception {
        super(info);
    }

    @Override
    public OdsModuleConfig getModuleConfig() {
        if (moduleConfig == null) {
            this.moduleConfig = new BagImportModuleConfig(this);
        }
        return moduleConfig;
    }
}
