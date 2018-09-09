package org.openstreetmap.josm.plugins.ods.bag.setup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.BagDownloader;
import org.openstreetmap.josm.plugins.ods.bag.os.storage.OdLigplaatsStore;
import org.openstreetmap.josm.plugins.ods.bag.os.storage.OdStandplaatsStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OdBuildingUnitStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.impl.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.AddressNodeMatcher;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.BuildingStatusAnalyzer;
import org.openstreetmap.josm.plugins.ods.domains.buildings.matching.StartYearAnalyzer;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OdLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.opendata.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.osm.OsmLayerManager;
import org.openstreetmap.josm.plugins.ods.entities.storage.OdEntityStore;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.OsmLayerDownloader;
import org.openstreetmap.josm.plugins.ods.matching.Matcher;
import org.openstreetmap.josm.plugins.ods.matching.OsmAnalyzer;
import org.openstreetmap.josm.plugins.ods.matching.OsmEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.setup.ModuleSetup;

public class BagModuleSetup implements ModuleSetup {
    //    private final GeoUtil geoUtil = new GeoUtil();
    //    private final CRSUtil crsUtil = new CRSUtilProj4j();
    private final EntityStores entityStores;
    private final OdLayerManager odLayerManager;
    private final OsmLayerManager osmLayerManager;
    private final BagOdSetup odSetup;
    private final BagOsmSetup osmSetup;
    private BagDownloader mainDownloader;


    public BagModuleSetup() {
        this.entityStores = new EntityStores();
        this.odLayerManager = new OdLayerManager("BAG ODS");
        this.osmLayerManager = new OsmLayerManager("BAG OSM");
        this.odSetup = new BagOdSetup(odLayerManager, entityStores);
        this.osmSetup = new BagOsmSetup(osmLayerManager, entityStores);
        setup();
    }

    private void setup() {
        Matchers matchers = setupMatchers(entityStores);
        OsmAnalyzer osmAnalyzer = createOsmAnalyzer(entityStores.osmBuilding);
        List<Runnable> processors = setupProcessors(matchers, osmAnalyzer);
        mainDownloader = new BagDownloader(this, processors);
    }


    private static List<Runnable> setupProcessors(Matchers matchers, OsmAnalyzer osmAnalyser) {
        List<Runnable> processors = new LinkedList<>();
        processors.addAll(matchers.all());
        processors.add(osmAnalyser);
        // TODO Auto-generated method stub
        return processors;
    }

    @Override
    public OdLayerManager getOdLayerManager() {
        return odLayerManager;
    }

    @Override
    public OsmLayerManager getOsmLayerManager() {
        return osmLayerManager;
    }

    @Override
    public OpenDataLayerDownloader getOdLayerDownloader() {
        return odSetup.getDownloader();
    }

    @Override
    public OsmLayerDownloader getOsmLayerDownloader() {
        return osmSetup.getOsmLayerDownloader();
    }

    @Override
    public MainDownloader getMainDownloader() {
        return mainDownloader;
    }

    private static Matchers setupMatchers(EntityStores stores) {
        Matchers matchers = new Matchers();
        matchers.building = new BuildingMatcher(stores.osmBuilding, stores.odBuilding);
        matchers.addressNode = new AddressNodeMatcher(stores.osmAddressNode, stores.odAddressNode);
        return matchers;
    }

    private static OsmAnalyzer createOsmAnalyzer(OsmBuildingStore osmBuildingStore) {
        OsmEntityAnalyzer<OsmBuilding> buildingAnalyzer = new OsmEntityAnalyzer<>(OsmBuilding.class, osmBuildingStore,
                new StartYearAnalyzer(), new BuildingStatusAnalyzer());
        return new OsmAnalyzer(Arrays.asList(
                buildingAnalyzer));
    }

    static class Matchers {
        public BuildingMatcher building;
        public AddressNodeMatcher addressNode;

        public List<? extends Matcher> all() {
            return Arrays.asList(building, addressNode);
        }
    }

    protected class EntityStores {
        final public OsmAddressNodeStore osmAddressNode;
        final public OsmBuildingStore osmBuilding;
        final public OdAddressNodeStore odAddressNode;
        final public OdBuildingUnitStore odBuildingUnit;
        final public OdBuildingStore odBuilding;
        final public OdLigplaatsStore odLigplaats;
        final public OdStandplaatsStore odStandplaats;

        public EntityStores() {
            this.odBuilding = new OdBuildingStore();
            this.odBuildingUnit = new OdBuildingUnitStore();
            this.odAddressNode = new OdAddressNodeStore();
            this.odLigplaats = new OdLigplaatsStore();
            this.odStandplaats = new OdStandplaatsStore();
            this.osmBuilding = new OsmBuildingStore();
            this.osmAddressNode = new OsmAddressNodeStore();
        }

        public List<OdEntityStore<?, ?>> odEntityStores() {
            return Arrays.asList(odAddressNode, odBuilding, odBuildingUnit);
        }
    }
}
