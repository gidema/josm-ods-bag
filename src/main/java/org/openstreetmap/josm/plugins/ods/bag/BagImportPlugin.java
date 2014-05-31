package org.openstreetmap.josm.plugins.ods.bag;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.ods.ConfigurationReader;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.OdsWorkingSet;
import org.openstreetmap.josm.plugins.ods.bag.actions.MarkObsoleteObjectsAction;
import org.openstreetmap.josm.plugins.ods.bag.actions.RemoveAssociatedStreetsAction;
import org.openstreetmap.josm.plugins.ods.bag.actions.UpdateBagTagsAction;
import org.openstreetmap.josm.plugins.ods.bag.osm.BagPrimitiveFactory;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.osm.PrimitiveFactory;
import org.openstreetmap.josm.tools.I18n;

public class BagImportPlugin extends OdsModulePlugin {
    private final static Bounds BOUNDS = new Bounds(50.734, 3.206, 53.583,
            7.245);
    private final static Map<Class<?>, EntityFactory<?>> FACTORIES = new HashMap<>();

    public BagImportPlugin(PluginInformation info) {
        super(info);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL configFile = classLoader.getResource("ods-bag.config.xml");
            try {
                ConfigurationReader configurationReader = new ConfigurationReader(
                        classLoader, this);
                configurationReader.read(configFile);
            } catch (ConfigurationException e) {
                Main.info("An error occured trying to registrate the odsFeatureSource types.");
                Main.info(e.getMessage());
                throw e;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ODS.registerModule(this);
        buildMenu();
        createFactories();
    }

    @Override
    public OdsWorkingSet getWorkingSet() {
        // TODO Auto-generated method stub
        return super.getWorkingSet();
    }

    private static void buildMenu() {
        JosmAction action = new RemoveAssociatedStreetsAction();
        action.setEnabled(false);
        ODS.getMenu().add(action);
        action = new MarkObsoleteObjectsAction();
        action.setEnabled(false);
        ODS.getMenu().add(action);
        action = new UpdateBagTagsAction();
        action.setEnabled(false);
        ODS.getMenu().add(action);
        // action = new FixOverlappingNodesAction();
        // action.setEnabled(false);
        // ODS.getMenu().add(action);
        // action = new BuildingPassageAction();
        // action.setEnabled(false);
        // ODS.getMenu().add(action);
    }

    private static void createFactories() {
        FACTORIES
                .put(SimpleFeature.class,
                        new org.openstreetmap.josm.plugins.ods.bag.gt.BagEntityFactory());
        FACTORIES
                .put(OsmPrimitive.class,
                        new org.openstreetmap.josm.plugins.ods.bag.osm.BagEntityFactory());
    }

    @Override
    public boolean usePolygonFile() {
        return true;
    }

    @Override
    public Bounds getBounds() {
        return BOUNDS;
    }

    @Override
    public String getName() {
        return "BAG";
    }

    @Override
    public String getDescription() {
        return I18n
                .tr("ODS module to import buildings and addresses in the Netherlands");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> EntityFactory<T> getEntityFactory(Class<T> clazz, String type) {
        return (EntityFactory<T>) FACTORIES.get(clazz);
    }

    @Override
    public PrimitiveFactory getPrimitiveFactory(DataSet dataSet) {
        return new BagPrimitiveFactory(dataSet);
    }

}
