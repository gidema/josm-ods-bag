package org.openstreetmap.josm.plugins.ods.bag;

//import static nl.gertjanidema.josm.bag.BAGDataType.ADRES;
//import static nl.gertjanidema.josm.bag.BAGDataType.LIGPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.PAND;
//import static nl.gertjanidema.josm.bag.BAGDataType.STANDPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.WEGVAK;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.ConfigurationReader;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.bag.actions.FixOverlappingNodesAction;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingPassageAction;
import org.openstreetmap.josm.tools.I18n;

public class BagImportPlugin extends OdsModulePlugin {
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583, 7.245);
    
    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @Override
    public Bounds getBounds() {
        return BOUNDS;
    }

    public BagImportPlugin(PluginInformation info) {
        super(info);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL configFile = classLoader.getResource("bagconfig.xml");
            try {
                ConfigurationReader configurationReader = new ConfigurationReader(
                        classLoader, this);
                configurationReader.read(configFile);
            } catch (ConfigurationException e) {
                Main.info("An error occured trying to registrate the odsFeatureSource types.");
                Main.info(e.getMessage());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ODS.registerModule(this);
        JosmAction action = new FixOverlappingNodesAction();
        action.setEnabled(false);
        ODS.getMenu().add(action);
//        action = new BuildingPassageAction();
//        action.setEnabled(false);
//        ODS.getMenu().add(action);        
    }

    @Override
    public String getName() {
        return "BAG";
    }

    @Override
    public String getDescription() {
        return I18n.tr("ODS module to import buildings and addresses in the Netherlands");
    }
}
