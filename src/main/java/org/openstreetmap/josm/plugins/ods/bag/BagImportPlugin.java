package org.openstreetmap.josm.plugins.ods.bag;

import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;

public class BagImportPlugin extends OdsModulePlugin {
    private OdsModule module;
    
    public BagImportPlugin(PluginInformation info) throws Exception {
        super(info);
    }

    @Override
    public OdsModule getModule() {
        if (module == null) {
            this.module = new BagImportModule(this);
        }
        return module;
    }
}
