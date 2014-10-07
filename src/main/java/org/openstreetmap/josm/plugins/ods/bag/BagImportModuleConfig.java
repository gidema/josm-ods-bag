package org.openstreetmap.josm.plugins.ods.bag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.PrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagBuildingTypeAnalyzer;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagAddressNodePrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagBuildingPrimitiveBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtilProj4j;
import org.openstreetmap.josm.plugins.ods.entities.MergeTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AlignBuildingsTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingSimplifier;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CheckBuildingCompletenessTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CreateAddressNodePrimitivesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CreateBuildingPrimitivesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.DistributeAddressNodesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GtAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.GtBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.MatchAddressToBuildingTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.OsmAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.OsmBuildingStore;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.external.GeotoolsDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloader;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.UniqueFeatureCollection;
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.OdsDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.tasks.Task;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagImportModuleConfig {
    private final OdsModulePlugin plugin;
    private final WFSHost host;
    private final CRSUtil crsUtil;
    private final GeoUtil geoUtil;
    private final InternalDataLayer internalDataLayer;
    private final ExternalDataLayer externalDataLayer;
    
    private final GtBuildingStore gtBuildingStore = new GtBuildingStore(); 
    private final GtAddressNodeStore gtAddressNodeStore = new GtAddressNodeStore(); 
    private final GtBuildingStore gtNewBuildingStore = new GtBuildingStore(); 
    private final GtAddressNodeStore gtNewAddressNodeStore = new GtAddressNodeStore(); 
    private final OsmBuildingStore osmBuildingStore = new OsmBuildingStore(); 
    private final OsmAddressNodeStore osmAddressNodeStore = new OsmAddressNodeStore(); 

    public BagImportModuleConfig(OdsModulePlugin plugin) {
        this.plugin = plugin;
        host = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
        geoUtil = new GeoUtil();
        crsUtil = new CRSUtilProj4j();

        internalDataLayer = new InternalDataLayer("BAG OSM");
        externalDataLayer = new ExternalDataLayer("BAG ODS");
    }

    public BagImportModule createModule() {
        return new BagImportModule(plugin, createOdsDownloader(), externalDataLayer, internalDataLayer);
    }
    
    private OdsDownloader createOdsDownloader() {
        Task buildingMatcher = new BuildingMatcher(gtBuildingStore, osmBuildingStore);
        List<Task> tasks = new ArrayList<>();
        tasks.add(buildingMatcher);
        return new OdsDownloader(createOsmDownloadJob(), createGeotoolsDownloadJob(), tasks);
    }
    
    private OsmDownloadJob createOsmDownloadJob() {
        List<Task> tasks = new ArrayList<>(0);
        List<OsmEntityBuilder<?>> builders = new LinkedList<>();
        builders.add(new BagOsmBuildingBuilder(geoUtil, osmBuildingStore));
        builders.add(new BagOsmAddressNodeBuilder(geoUtil, osmAddressNodeStore));
        OsmDownloader downloader = new OsmDownloader(builders);
        return new OsmDownloadJob(internalDataLayer, downloader, tasks);
    }

    private GeotoolsDownloadJob createGeotoolsDownloadJob() {
        List<Downloader> downloaders = new LinkedList<>();
        downloaders.add(createGtPandDownloader());
        downloaders.add(createGtLigplaatsDownloader());
        downloaders.add(createGtStandplaatsDownloader());
        downloaders.add(createGtVerblijfsobjectDownloader());
        PrimitiveBuilder<Building> buildingPrimitiveBuilder = 
              new BagBuildingPrimitiveBuilder(externalDataLayer.getOsmDataLayer().data);
        PrimitiveBuilder<AddressNode> addressNodePrimitiveBuilder = 
              new BagAddressNodePrimitiveBuilder(externalDataLayer.getOsmDataLayer().data);
        List<Task> tasks = new LinkedList<>();
        tasks.add(new MatchAddressToBuildingTask(gtNewBuildingStore, gtNewAddressNodeStore));
        tasks.add(new CheckBuildingCompletenessTask(gtNewBuildingStore));
        tasks.add(new DistributeAddressNodesTask(geoUtil, gtNewBuildingStore));
        tasks.add(new BagBuildingTypeAnalyzer(gtNewBuildingStore));
        tasks.add(new MergeTask<>(gtNewBuildingStore, gtBuildingStore));
        tasks.add(new MergeTask<>(gtNewAddressNodeStore, gtAddressNodeStore));
        tasks.add(new CreateAddressNodePrimitivesTask(gtNewAddressNodeStore, addressNodePrimitiveBuilder));
        tasks.add(new CreateBuildingPrimitivesTask(gtNewBuildingStore, buildingPrimitiveBuilder));
        return new GeotoolsDownloadJob(externalDataLayer, downloaders, tasks);
    }
    
    
    private GtDownloader createGtPandDownloader() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new BuildingSimplifier(gtNewBuildingStore, 2e-7));
        tasks.add(new AlignBuildingsTask(gtNewBuildingStore, geoUtil, 2e-7));
        return createGtBuildingDownloader("bag:pand", tasks);
    }
    
    private GtDownloader createGtLigplaatsDownloader() {
        return createGtBuildingDownloader("bag:ligplaats", null);
    }
    
    private GtDownloader createGtStandplaatsDownloader() {
        return createGtBuildingDownloader("bag:standplaats", null);
    }
    
    private GtDownloader createGtVerblijfsobjectDownloader() {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(crsUtil, gtNewAddressNodeStore);
        UniqueFeatureCollection ufc = new UniqueFeatureCollection("identificatie");
        GtFeatureSource featureSource = new GtFeatureSource(host, "bag:verblijfsobject");
        GtDataSource dataSource = new GtDataSource(featureSource);
        return new GtDownloader(dataSource, ufc, crsUtil, entityBuilder, gtNewAddressNodeStore, null);
    }
    
    private GtDownloader createGtBuildingDownloader(String featureType, List<Task> tasks) {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(crsUtil, gtNewBuildingStore);
        UniqueFeatureCollection ufc = new UniqueFeatureCollection("identificatie");
        GtFeatureSource featureSource = new GtFeatureSource(host, featureType);
        GtDataSource dataSource = new GtDataSource(featureSource);
        return new GtDownloader(dataSource, ufc, crsUtil, entityBuilder, gtNewBuildingStore, 
                (tasks == null ? new ArrayList<Task>(0) : tasks));
        
    }
}
