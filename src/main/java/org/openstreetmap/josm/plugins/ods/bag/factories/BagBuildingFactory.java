package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.BAGBuildingType;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagBuildingImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;


public class BagBuildingFactory implements OdEntityFactory {
    private static QName typeName = new QName(BagPdok.NS_BAG, "pand");
    private final BagBuildingStore buildingStore;

    public BagBuildingFactory(OdsContext context) {
        this.buildingStore = context.getComponent(BagBuildingStore.class);
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
        building.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        building.setBuildingType(BAGBuildingType.UNCLASSIFIED);
        building.setAantalVerblijfsobjecten(FeatureUtil.getLong(feature, "aantal_verblijfsobjecten"));
        building.setGeometry(feature.getGeometry());
        buildingStore.add(building);
    }

    public static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Bouwvergunning verleend":
            return EntityStatus.PLANNED;
        case "Bouw gestart":
            return EntityStatus.CONSTRUCTION;
        case "Pand in gebruik":
        case "Pand buiten gebruik":
            return EntityStatus.IN_USE;
        case "Pand in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE_NOT_MEASURED;
        case "Niet gerealiseerd pand":
            return EntityStatus.NOT_REALIZED;
        case "Sloopvergunning verleend":
            return EntityStatus.REMOVAL_DUE;
        case "Pand gesloopt":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.UNKNOWN;
        }
    }
}
