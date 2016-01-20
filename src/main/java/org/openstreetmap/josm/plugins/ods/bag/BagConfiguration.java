package org.openstreetmap.josm.plugins.ods.bag;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.OdsModuleConfiguration;
import org.openstreetmap.josm.plugins.ods.geotools.GroupByQuery;
import org.openstreetmap.josm.plugins.ods.geotools.GtDataSource;
import org.openstreetmap.josm.plugins.ods.geotools.GtFeatureSource;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.plugins.ods.wfs.WFSHost;

import exceptions.OdsException;

public class BagConfiguration implements OdsModuleConfiguration {
    private WFSHost bagHost = new WFSHost("BAG WFS", "http://geodata.nationaalgeoregister.nl/bag/wfs", 15000);
//    private WFSHost demolishedBuildingsHost = new WFSHost("Demolished", "http://duinoord.xs4all.nl:8080/geoserver/wfs", 15000);
    private Map<String, Host> hosts;
    
    private GtFeatureSource vboFeatureSource = new GtFeatureSource(bagHost, "bag:verblijfsobject", "identificatie");
    private GtFeatureSource pandFeatureSource = new GtFeatureSource(bagHost, "bag:pand", "identificatie");
    private GtFeatureSource ligplaatsFeatureSource = new GtFeatureSource(bagHost, "bag:ligplaats", "identificatie");
    private GtFeatureSource standplaatsFeatureSource = new GtFeatureSource(bagHost, "bag:standplaats", "identificatie");
//    private GtFeatureSource geslooptPandFeatureSource = new GtFeatureSource(demolishedBuildingsHost, "osm_bag:buildingdestroyed_osm", "identificatie");

    private List<GtFeatureSource> featureSources = Arrays.asList(
            vboFeatureSource,
            pandFeatureSource,
            ligplaatsFeatureSource,
            standplaatsFeatureSource);
//    ,
//            geslooptPandFeatureSource);
    
    private GtDataSource vboDataSource = createVboDataSource(vboFeatureSource);
    private GtDataSource pandDataSource = createPandDataSource(pandFeatureSource);
    private GtDataSource ligplaatsDataSource = createPandDataSource(ligplaatsFeatureSource);
    private GtDataSource standplaatsDataSource = createPandDataSource(standplaatsFeatureSource);
//    private GtDataSource geslooptPandDataSource = createPandDataSource(geslooptPandFeatureSource);

    private Map<String, OdsDataSource> dataSources;

    public BagConfiguration() {
        hosts = new HashMap<>();
        hosts.put(bagHost.getName(), bagHost);
//        hosts.put(demolishedBuildingsHost.getName(), demolishedBuildingsHost);
        
        dataSources = new HashMap<>();
        dataSources.put(vboDataSource.getFeatureType(), vboDataSource);
        dataSources.put(pandDataSource.getFeatureType(), pandDataSource);
        dataSources.put(ligplaatsDataSource.getFeatureType(), ligplaatsDataSource);
        dataSources.put(standplaatsDataSource.getFeatureType(), standplaatsDataSource);
//        dataSources.put(geslooptPandDataSource.getFeatureType(), geslooptPandDataSource);
    }
    
    /**
     * @see org.openstreetmap.josm.plugins.ods.bag.OdsModuleConfiguration#getHosts()
     */
    @Override
    public Collection<Host> getHosts() {
        return hosts.values();
    }
    
    @Override
    public List<? extends OdsFeatureSource> getFeatureSources() {
        return featureSources;
    }
    
    @Override
    public Collection<OdsDataSource> getDataSources() {
        return dataSources.values();
    }

    @Override
    public OdsDataSource getDataSource(String name) throws OdsException {
        OdsDataSource dataSource = dataSources.get(name);
        if (dataSource == null) {
            String msg = String.format("Unknown feature type: %s", name);
            throw new OdsException(msg);
        }
        return dataSource;
    }

    private static GtDataSource createVboDataSource(GtFeatureSource vboFeatureSource) {
        Query query = new GroupByQuery(vboFeatureSource, Arrays.asList("identificatie", "pandidentificatie"));
        return new GtDataSource(vboFeatureSource, query);
    }
    
    private static GtDataSource createPandDataSource(GtFeatureSource featureSource) {
        Query query = new GroupByQuery(featureSource, Arrays.asList("identificatie"));
        return new GtDataSource(featureSource, query);
    }
}
