package org.openstreetmap.josm.plugins.ods.bag;

//import static nl.gertjanidema.josm.bag.BAGDataType.ADRES;
//import static nl.gertjanidema.josm.bag.BAGDataType.LIGPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.PAND;
//import static nl.gertjanidema.josm.bag.BAGDataType.STANDPLAATS;
//import static nl.gertjanidema.josm.bag.BAGDataType.WEGVAK;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.ConfigurationReader;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.tools.I18n;

public class BagImportPlugin extends Plugin implements OdsModule {

    public BagImportPlugin(PluginInformation info) {
        super(info);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL configFile = classLoader.getResource("config.xml");
            try {
                ConfigurationReader configurationReader = new ConfigurationReader(
                        classLoader);
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
