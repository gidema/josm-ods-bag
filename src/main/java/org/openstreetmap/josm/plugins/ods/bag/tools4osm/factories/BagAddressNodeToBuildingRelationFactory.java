package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagAddressNode_BuildingPair;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNode2BuildingPairStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagAddressNodeToBuildingRelationFactory implements OdEntityFactory {
    private final QName typeName = new QName(BagPdok.NS_BAG, "verblijfsobject");
    private final BagAddressNode2BuildingPairStore entityStore;

    public BagAddressNodeToBuildingRelationFactory(OdsContext context) {
        entityStore = context.getComponent(BagAddressNode2BuildingPairStore.class);
    }
    
    @Override
    public boolean appliesTo(QName featureType) {
        // TODO Auto-generated method stub
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        Long addressNodeId = FeatureUtil.getLong(feature, "identificatie");
        Long buildingId = FeatureUtil.getLong(feature, "pandidentificatie");
        BagAddressNode_BuildingPair pair = new BagAddressNode_BuildingPair(addressNodeId, buildingId);
        entityStore.add(pair);
    }
}
