package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuilding;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagBuildingUnit;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagBuildingUnitStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.match.AddressNodeMatch;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagBuildingUnitFactory implements OdEntityFactory {
    private final QName typeName = new QName(BagPdok.NS_BAG, "verblijfsobject");
    private final BagBuildingUnitStore buildingUnitStore;
    private final BagAddressNodeStore addressNodeStore;

    public BagBuildingUnitFactory(OdsContext context) {
        this.buildingUnitStore = context.getComponent(BagBuildingUnitStore.class);
        this.addressNodeStore = context.getComponent(BagAddressNodeStore.class);
    }
    
    @Override
    public boolean appliesTo(QName featureType) {
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        BagVerblijfsobjectImpl buildingUnit = new BagVerblijfsobjectImpl();
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            buildingUnit.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        String id = FeatureUtil.getString(feature, "identificatie");
        buildingUnit.setBuildingUnitId(Long.valueOf(id));
        buildingUnit.setSource("BAG");
        buildingUnit.setGeometry(feature.getGeometry());
        NlAddressImpl address = createAddress(feature);
        BagOdAddressNode addressNode = new BagOdAddressNode();
        addressNode.setAddress(address);
        addressNode.setGeometry(buildingUnit.getGeometry());
        addressNode.setAddressId(buildingUnit.getBuildingUnitId());
        addressNode.setBuilding(buildingUnit.getBuilding());
        addressNodeStore.add(addressNode);
        addressNode.setBuildingUnit(buildingUnit);
        addressNode.setSourceDate(buildingUnit.getSourceDate());
        buildingUnit.setMainAddressNode(addressNode);
        buildingUnit.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        addressNode.setStatus(buildingUnit.getStatus());
        buildingUnit.setGebruiksdoel(FeatureUtil.getString(feature, "gebruiksdoel"));
        buildingUnit.setArea(FeatureUtil.getBigInteger(feature, "oppervlakte").doubleValue());
        buildingUnitStore.add(buildingUnit);
    }

    public static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Verblijfsobject gevormd":
            return EntityStatus.PLANNED;
        case "Verblijfsobject in gebruik":
        case "Verblijfsobject buiten gebruik":
        case "Verblijfsobject in gebruik (niet ingemeten)":
            return EntityStatus.IN_USE;
        case "Verblijfsobject ingetrokken":
        case "Niet gerealiseerd verblijfsobject":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.IN_USE;
        }
    }
    
    public NlAddressImpl createAddress(WfsFeature feature) {
        NlAddressImpl address = new NlAddressImpl();
        address.setHouseNumber(createHouseNumber(feature));
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        return address;
    }
    
    private NlHouseNumber createHouseNumber(WfsFeature feature) {
        Integer number = Integer.valueOf(FeatureUtil.getString(feature, "huisnummer"));
        Character houseLetter = FeatureUtil.getCharacter(feature, "huisletter");
        String houseNumberExtra = FeatureUtil.getString(feature, "toevoeging");
        return new NlHouseNumberImpl(number, houseLetter, houseNumberExtra);
    }

    static class BagVerblijfsobjectImpl extends AbstractOdEntity implements BagBuildingUnit {
        private Long buildingUnitId;
        private double area;
        private String gebruiksdoel;
        private BagBuilding building;
        private OdAddressNode mainAddressNode;
        
        public void setMainAddressNode(OdAddressNode addressNode) {
            this.mainAddressNode = addressNode;
        }

        public Long getBuildingUnitId() {
            return buildingUnitId;
        }

        public void setBuildingUnitId(Long buildingUnitId) {
            this.buildingUnitId = buildingUnitId;
        }

        public void setArea(double area) {
            this.area = area;
        }

        public void setGebruiksdoel(String gebruiksdoel) {
            this.gebruiksdoel = gebruiksdoel;
        }

        @Override
        public String getGebruiksdoel() {
            return gebruiksdoel;
        }

        @Override
        public double getArea() {
            return area;
        }

        @Override
        public AddressNodeMatch getMatch() {
            // TODO Auto-generated method stub
            return null;
        }

//        @Override
//        public NLAddress getAddress() {
//            return address;
//        }
//
        @Override
        public BagBuilding getBuilding() {
            return building;
        }

        @Override
        public void setBuilding(BagBuilding building) {
            this.building = building;
        }

        @Override
        public void setMatch(AddressNodeMatch match) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public OdAddressNode getMainAddressNode() {
            return mainAddressNode;
        }

        @Override
        public boolean readyForImport() {
            // Building units are not directly related to OSM primitives.
            // This means they cannot be imported to the OSM layer.
            return false;
        }
    }
}
