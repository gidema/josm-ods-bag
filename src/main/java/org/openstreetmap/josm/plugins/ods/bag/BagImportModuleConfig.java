package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfig;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.OsmDownloadJob;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloader;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.geotools.UniqueFeatureCollection;
import org.openstreetmap.josm.plugins.ods.io.OdsDownloader;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

import com.google.inject.Provides;
import com.google.inject.Singleton;

public class BagImportModuleConfig extends OdsModuleConfig {
    
    public BagImportModuleConfig(OdsModulePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(OdsModule.class).to(BagImportModule.class).in(Singleton.class);
        bind(EntityFactory.class).to(BagEntityFactory.class);
        bind(OsmDownloadJob.class).in(Singleton.class);
        bind(OdsDownloader.class).in(Singleton.class);
        WFSHost host = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
        bind(WFSHost.class).toInstance(host);
    }
    
//    @Provides @Deprecated
//    private static List<ExternalDownloadTask> externalDownloadTasksProvider(WFSHost host) {
//        List<ExternalDownloadTask> downloadTasks = new LinkedList<>();
//        downloadTasks.add(createGtDownloadTask(host, "bag:pand"));
//        downloadTasks.add(createGtDownloadTask(host, "bag:verblijfsobject"));
//        downloadTasks.add(createGtDownloadTask(host, "bag:ligplaats"));
//        downloadTasks.add(createGtDownloadTask(host, "bag:standplaats"));
//        return downloadTasks;
//    }
    
    @Provides
    private static List<GtDownloader> gtFeatureDownloadersProvider(WFSHost host, CRSUtil crsUtil) {
        List<GtDownloader> downloaders = new LinkedList<>();
        downloaders.add(createGtDownloader(host, "bag:pand", crsUtil));
        downloaders.add(createGtDownloader(host, "bag:verblijfsobject", crsUtil));
        downloaders.add(createGtDownloader(host, "bag:ligplaats", crsUtil));
        downloaders.add(createGtDownloader(host, "bag:standplaats", crsUtil));
        return downloaders;
    }
    
    @Provides @Singleton
    static ExternalDataLayer externalDataLayerProvider() {
        return new ExternalDataLayer("BAG ODS");
    }
    
    @Provides @Singleton
    static InternalDataLayer internalDataLayerProvider() {
        return new InternalDataLayer("BAG OSM");
    }
    
    static GtDownloader createGtDownloader(WFSHost host, String feature, CRSUtil crsUtil) {
        UniqueFeatureCollection ufc = new UniqueFeatureCollection("identificatie");
        GtFeatureSource featureSource = new GtFeatureSource(host, feature);
        GtDataSource dataSource = new GtDataSource(featureSource);
        return new GtDownloader(dataSource, ufc, crsUtil);
    }
}
