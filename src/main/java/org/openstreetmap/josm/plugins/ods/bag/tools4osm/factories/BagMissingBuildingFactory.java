package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagBuildingImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMissingBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.factories.BagBuildingFactory;
import org.openstreetmap.josm.plugins.ods.bag.tools4osm.BagTools4Osm;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;


public class BagMissingBuildingFactory implements OdEntityFactory {
    private static QName typeName = new QName(BagTools4Osm.NS_TOOSL4OSM, "Building_Missing");
    private final BagMissingBuildingStore buildingStore;

    public BagMissingBuildingFactory(OdsContext context) {
        this.buildingStore = context.getComponent(BagMissingBuildingStore.class);
    }
    @Override
    public boolean appliesTo(QName featureType) {
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        BagBuildingImpl building = new BagBuildingImpl();
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            building.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        String id = FeatureUtil.getString(feature, "identificatie");
        building.setBuildingId(Long.valueOf(id));
        building.setSource("BAG");
        building.setStatus(BagBuildingFactory.parseStatus(FeatureUtil.getString(feature, "pandstatus")));
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        building.setStartYear(bouwjaar);
        building.setBuildingType(BAGBuildingType.UNCLASSIFIED);
        building.setGeometry(feature.getGeometry());
        buildingStore.add(building);
    }
}
