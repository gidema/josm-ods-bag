package org.openstreetmap.josm.plugins.ods.bag;

import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.DefaultOdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsDownloader;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfig;
import org.openstreetmap.josm.plugins.ods.OdsModulePlugin;
import org.openstreetmap.josm.plugins.ods.entities.EntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalDownloadTask;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDataLayer;
import org.openstreetmap.josm.plugins.ods.entities.internal.InternalDownloadJob;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtDownloadTask;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class BagImportModuleConfig extends OdsModuleConfig {
    
    public BagImportModuleConfig(OdsModulePlugin plugin) {
        super(plugin);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(OdsModule.class).to(BagImportModule.class).in(Singleton.class);
        bind(EntityFactory.class).to(BagEntityFactory.class);
        bind(InternalDownloadJob.class).in(Singleton.class);
        bind(OdsDownloader.class).in(Singleton.class);
        WFSHost host = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
        bind(WFSHost.class).toInstance(host);
    }
    
    @Provides
    private List<ExternalDownloadTask> externalDownloadTasksProvider(WFSHost host) {
        List<ExternalDownloadTask> downloadTasks = new LinkedList<>();
        downloadTasks.add(createGtDownloadTask(host, "bag:pand"));
        downloadTasks.add(createGtDownloadTask(host, "bag:verblijfsobject"));
        downloadTasks.add(createGtDownloadTask(host, "bag:ligplaats"));
        downloadTasks.add(createGtDownloadTask(host, "bag:standplaats"));
        return downloadTasks;
    }
    
    @Provides @Singleton
    ExternalDataLayer externalDataLayerProvider() {
        return new ExternalDataLayer("BAG ODS");
    }
    
    @Provides @Singleton
    InternalDataLayer internalDataLayerProvider() {
        return new InternalDataLayer("BAG OSM");
    }
    
    @Provides
    GtDownloadTask createGtDownloadTask(WFSHost host, String feature) {
        GtFeatureSource featureSource = new GtFeatureSource(host, feature);
        GtDataSource pandDataSource = new GtDataSource(featureSource);
        return new GtDownloadTask(pandDataSource);
    }
}
