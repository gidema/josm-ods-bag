package org.openstreetmap.josm.plugins.ods.bag.factories;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BuildingUnit_BuildingPair;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnit2BuildingPairStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BuildingUnitToBuildingRelationFactory implements OdEntityFactory {
    private final QName typeName = new QName(BagPdok.NS_BAG, "verblijfsobject");
    private final BagBuildingUnit2BuildingPairStore entityStore;

    public BuildingUnitToBuildingRelationFactory(OdsContext context) {
        entityStore = context.getComponent(BagBuildingUnit2BuildingPairStore.class);
    }
    
    @Override
    public boolean appliesTo(QName featureType) {
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        Long id = FeatureUtil.getLong(feature, "identificatie");
        Long buildingId = FeatureUtil.getLong(feature, "pandidentificatie");
        BuildingUnit_BuildingPair pair = new BuildingUnit_BuildingPair(id, buildingId);
        entityStore.add(pair);
    }
}
