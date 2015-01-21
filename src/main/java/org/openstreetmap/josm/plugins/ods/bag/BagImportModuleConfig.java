package org.openstreetmap.josm.plugins.ods.bag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
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
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.BuildingMatcher;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CheckBuildingCompletenessTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CreateAddressNodePrimitivesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.CreateBuildingPrimitivesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.DistributeAddressNodesTask;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.FindBuildingNeighboursTask;
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
import org.openstreetmap.josm.plugins.ods.io.Downloader;
import org.openstreetmap.josm.plugins.ods.io.OdsDownloader;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;
import org.openstreetmap.josm.plugins.ods.objects.builtenvironment.AlignBuildingsTask;
import org.openstreetmap.josm.plugins.ods.objects.builtenvironment.SimplifyBuildingSegmentsTask;
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
    private final OsmBuildingStore osmBuildingStore = new OsmBuildingStore(); 
    private final OsmAddressNodeStore osmAddressNodeStore = new OsmAddressNodeStore(); 

    private final Filter pandFilter;
    private final Filter vboFilter;
    
    { 
        try {
            pandFilter = CQL.toFilter("status <> 'Niet gerealiseerd pand' AND status <> 'Bouwvergunning verleend' " +
                    "AND status <> 'Pand gesloopt' AND status <> 'Pand buiten gebruik'");
            vboFilter = CQL.toFilter("status <> 'Verblijfsobject buiten gebruik' AND " +
                    "status <> 'Niet gerealiseerd verblijfsobject' AND status <> 'Verblijfsobject ingetrokken'");
        } catch (CQLException e) {
            throw new RuntimeException();
        }
    }
    
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
        double tolerance = 0.05;
        List<Downloader> downloaders = new LinkedList<>();
        downloaders.add(createGtPandDownloader());
        downloaders.add(createGtLigplaatsDownloader());
        downloaders.add(createGtStandplaatsDownloader());
        downloaders.add(createGtVerblijfsobjectDownloader());
        PrimitiveBuilder<Building> buildingPrimitiveBuilder = 
              new BagBuildingPrimitiveBuilder(externalDataLayer);
        PrimitiveBuilder<AddressNode> addressNodePrimitiveBuilder = 
              new BagAddressNodePrimitiveBuilder(externalDataLayer);
        List<Task> tasks = new LinkedList<>();
        tasks.add(new MatchAddressToBuildingTask(gtBuildingStore, gtAddressNodeStore));
        tasks.add(new CheckBuildingCompletenessTask(gtBuildingStore));
        tasks.add(new DistributeAddressNodesTask(geoUtil, gtBuildingStore));
        tasks.add(new BagBuildingTypeAnalyzer(gtBuildingStore));
        // TODO the FindBuildingNeighboursTask is currently running with LatLon coordinates;
        // Can we find an efficient way to run this with EastNorth coordinate instead?
        tasks.add(new FindBuildingNeighboursTask(gtBuildingStore, geoUtil, 2e-7));
        tasks.add(new CreateAddressNodePrimitivesTask(gtAddressNodeStore, addressNodePrimitiveBuilder));
        tasks.add(new CreateBuildingPrimitivesTask(gtBuildingStore, buildingPrimitiveBuilder));
        tasks.add(new SimplifyBuildingSegmentsTask(gtBuildingStore, tolerance));
        tasks.add(new AlignBuildingsTask(gtBuildingStore, tolerance));
        return new GeotoolsDownloadJob(externalDataLayer, downloaders, tasks);
    }
    
    
    private GtDownloader createGtPandDownloader() {
        List<Task> tasks = new ArrayList<>();
        return createGtBuildingDownloader("bag:pand", tasks);
    }
    
    private GtDownloader createGtLigplaatsDownloader() {
        return createGtBuildingDownloader("bag:ligplaats", null);
    }
    
    private GtDownloader createGtStandplaatsDownloader() {
        return createGtBuildingDownloader("bag:standplaats", null);
    }
    
    private GtDownloader createGtVerblijfsobjectDownloader() {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(crsUtil, gtAddressNodeStore);
        GtFeatureSource featureSource = new GtFeatureSource(host, "bag:verblijfsobject");
        GtDataSource dataSource = new GtDataSource(featureSource, vboFilter);
        return new GtDownloader(dataSource, crsUtil, entityBuilder, gtAddressNodeStore, null);
    }
    
    private GtDownloader createGtBuildingDownloader(String featureType, List<Task> tasks) {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(crsUtil, gtBuildingStore);
        GtFeatureSource featureSource = new GtFeatureSource(host, featureType);
        GtDataSource dataSource = new GtDataSource(featureSource, pandFilter);
        return new GtDownloader(dataSource, crsUtil, entityBuilder, gtBuildingStore, 
                (tasks == null ? new ArrayList<Task>(0) : tasks));
    }
}
