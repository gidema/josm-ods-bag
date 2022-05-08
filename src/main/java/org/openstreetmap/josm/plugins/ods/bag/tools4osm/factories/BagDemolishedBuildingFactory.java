package org.openstreetmap.josm.plugins.ods.bag.tools4osm.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagBuildingImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagDemolishedBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.tools4osm.BagTools4Osm;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;


public class BagDemolishedBuildingFactory implements OdEntityFactory {
    private static QName typeName = new QName(BagTools4Osm.NS_TOOSL4OSM, "Building_Destroyed");
    private final BagDemolishedBuildingStore buildingStore;

    public BagDemolishedBuildingFactory(OdsContext context) {
        this.buildingStore = context.getComponent(BagDemolishedBuildingStore.class);
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
        Integer bouwjaar = FeatureUtil.getInteger(feature, "bouwjaar");
        building.setStartYear(bouwjaar);
        building.setStatus(EntityStatus.REMOVED);
        building.setBuildingType(BAGBuildingType.UNCLASSIFIED);
        building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        building.setGeometry(feature.getGeometry());
        buildingStore.add(building);
    }
}
