package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.gt.build.BagGtBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmAddressNodeBuilder;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.BagOsmBuildingBuilder;
import org.openstreetmap.josm.plugins.ods.entities.actual.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.actual.Building;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureDownloader;
import org.openstreetmap.josm.plugins.ods.entities.external.OpenDataLayerDownloader;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloaderNg;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmEntityBuilder;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.MainDownloader;
import org.openstreetmap.josm.plugins.ods.io.LayerDownloader;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

public class BagDownloader extends MainDownloader {
    private static WFSHost wfsHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);

    final private OdsModule module;
    
    public BagDownloader(OdsModule module) {
//        super(osmDownloadJob, geotoolsDownloadJob, postDownloadTasks);
        this.module = module;
    }

    private final Filter pandFilter;
    private final Filter vboFilter;
    
    { 
        try {
            pandFilter = CQL.toFilter("status <> 'Niet gerealiseerd pand' AND status <> 'Bouwvergunning verleend' " +
                    "AND status <> 'Pand gesloopt' AND status <> 'Pand buiten gebruik'");
            vboFilter = CQL.toFilter("status <> 'Verblijfsobject buiten gebruik' AND " +
                    "status <> 'Niet gerealiseerd verblijfsobject' AND status <> 'Verblijfsobject ingetrokken'");
        } catch (CQLException e) {
            throw new RuntimeException(e);
        }
    }

    private LayerDownloader osmDownloader;
    @Override
    protected LayerDownloader getOsmLayerDownloader() {
        if (osmDownloader == null) {
            List<OsmEntityBuilder<?>> builders = new LinkedList<>();
            Consumer<Building> buildingConsumer = module.getDataManager().getBuildingManager().getOsmBuildingConsumer();
            Consumer<AddressNode> addressNodeConsumer = module.getDataManager().getAddressNodeManager().getOsmAddressNodeConsumer();
            builders.add(new BagOsmBuildingBuilder(module.getGeoUtil(), buildingConsumer));
            builders.add(new BagOsmAddressNodeBuilder(module.getGeoUtil(), addressNodeConsumer));
            osmDownloader = new OsmDownloaderNg(module.getInternalDataLayer(), builders);
        }
        return osmDownloader;
    }

    private LayerDownloader geotoolsDownloader;
    @Override
    public LayerDownloader getOpenDataLayerDownloader() {
        if (geotoolsDownloader == null) {
            //double tolerance = 0.05;
            List<FeatureDownloader> downloaders = new LinkedList<>();
            downloaders.add(createPandDownloader());
            downloaders.add(createLigplaatsDownloader());
            downloaders.add(createStandplaatsDownloader());
            downloaders.add(createVerblijfsobjectDownloader());
            return new OpenDataLayerDownloader(module.getExternalDataLayer(), downloaders, null);
        }
        return geotoolsDownloader;
    }
    
    private FeatureDownloader createPandDownloader() {
        return createBuildingDownloader("bag:pand");
    }
    
    private FeatureDownloader createLigplaatsDownloader() {
        return createBuildingDownloader("bag:ligplaats");
    }
    
    private FeatureDownloader createStandplaatsDownloader() {
        return createBuildingDownloader("bag:standplaats");
    }
    
    private FeatureDownloader createVerblijfsobjectDownloader() {
        BagGtAddressNodeBuilder entityBuilder = new BagGtAddressNodeBuilder(module.getCrsUtil(), 
            module.getDataManager().getAddressNodeManager().getOsmAddressNodeConsumer());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, "bag:verblijfsobject", "identificatie");
        GtDataSource dataSource = new GtDataSource(featureSource, vboFilter);
        return new GtDownloader(dataSource, module.getCrsUtil(), entityBuilder, null);
    }
    
    private FeatureDownloader createBuildingDownloader(String featureType) {
        BagGtBuildingBuilder entityBuilder = new BagGtBuildingBuilder(module.getCrsUtil(), 
            module.getDataManager().getBuildingManager().getOpenDataBuildingConsumer());
        GtFeatureSource featureSource = new GtFeatureSource(wfsHost, featureType, "identificatie");
        GtDataSource dataSource = new GtDataSource(featureSource, pandFilter);
        return new GtDownloader(dataSource, module.getCrsUtil(), entityBuilder, null);
    }
}
