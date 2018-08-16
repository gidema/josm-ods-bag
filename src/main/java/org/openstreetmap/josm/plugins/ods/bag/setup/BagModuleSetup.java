package org.openstreetmap.josm.plugins.ods.bag.setup;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.bag.BagDownloader;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
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
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.matching.Matcher;
import org.openstreetmap.josm.plugins.ods.matching.OsmAnalyzer;
import org.openstreetmap.josm.plugins.ods.matching.OsmEntityAnalyzer;
import org.openstreetmap.josm.plugins.ods.setup.ModuleSetup;

public class BagModuleSetup implements ModuleSetup {
    private final GeoUtil geoUtil = new GeoUtil();
    private final CRSUtil crsUtil = new CRSUtilProj4j();
    private final EntityStores entityStores;
    private final OdLayerManager odLayerManager;
    private final OsmLayerManager osmLayerManager;
    private final BagOdSetup odSetup;
    private final BagOsmSetup osmSetup;
    private BagDownloader mainDownloader;


    public BagModuleSetup() {
        this.entityStores = setupEntityStores();
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

    private EntityStores setupEntityStores() {
        EntityStores stores = new EntityStores();
        stores.odBuilding = new OdBuildingStore();
        stores.odBuildingUnit = new OdBuildingUnitStore();
        stores.odAddressNode = new OdAddressNodeStore();
        stores.osmBuilding = new OsmBuildingStore();
        stores.osmAddressNode = new OsmAddressNodeStore();
        return stores;
    }

    protected class EntityStores {
        public OsmAddressNodeStore osmAddressNode;
        public OsmBuildingStore osmBuilding;
        public OdAddressNodeStore odAddressNode;
        public OdBuildingUnitStore odBuildingUnit;
        public OdBuildingStore odBuilding;

        public List<OdEntityStore<?, ?>> odEntityStores() {
            return Arrays.asList(odAddressNode, odBuilding, odBuildingUnit);
        }
    }
}
