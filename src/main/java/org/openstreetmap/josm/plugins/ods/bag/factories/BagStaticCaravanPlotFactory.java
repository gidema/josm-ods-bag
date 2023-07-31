package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.BagImportPlugin;
import org.openstreetmap.josm.plugins.ods.bag.BagPreferences;
import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagStaticCaravanPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.BagOdAddressNode;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagAddressNodeStore;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagStaticCaravanPlotStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagStaticCaravanPlotFactory implements OdEntityFactory {
    private final BagPreferences preferences;
    private static QName typeName = new QName(BagPdok.NS_BAG, "standplaats");
    private final BagStaticCaravanPlotStore standplaatsStore;
    private final BagAddressNodeStore addressNodeStore;

    public BagStaticCaravanPlotFactory(OdsContext context) {
        this.preferences = BagImportPlugin.getPreferences();
        this.standplaatsStore = context.getComponent(BagStaticCaravanPlotStore .class);
        this.addressNodeStore = context.getComponent(BagAddressNodeStore.class);
    }

    @Override
    public boolean appliesTo(QName featureType) {
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        BagStandplaatsImpl standplaats = new BagStandplaatsImpl();
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            standplaats.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        String id = FeatureUtil.getString(feature, "identificatie");
        standplaats.setId(Long.valueOf(id));
        standplaats.setSource("BAG");
        standplaats.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        standplaats.setGeometry(feature.getGeometry());
        NlHouseNumber houseNumber = new NlHouseNumberImpl(
            FeatureUtil.getInteger(feature, "huisnummer"),
            FeatureUtil.getCharacter(feature, "huisletter"),
            FeatureUtil.getString(feature, "toevoeging"));
        NlAddressImpl address = new NlAddressImpl();
        address.setHouseNumber(houseNumber);
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        if (preferences.isStaticCaravanAddressNode()) {
            BagOdAddressNode addressNode = new BagOdAddressNode();
            addressNode.setAddress(address);
            addressNode.setGeometry(standplaats.getGeometry().getCentroid());
            addressNode.setAddressId(standplaats.getId());
            addressNode.setAddressableObject(standplaats);
            addressNode.setSourceDate(standplaats.getSourceDate());
            // TODO Consider replacing setBuilding with setAddressable
//            addressNode.setBuilding(buildingUnit.getBuilding());
            addressNodeStore.add(addressNode);
        }
        else {
            standplaats.setAddress(address);
        }
        standplaatsStore.add(standplaats);
    }

    private static PlotStatus parseStatus(String status) {
        switch (status) {
        case "Plaats aangewezen":
            return PlotStatus.ASSIGNED;
        case "Plaats ingetrokken":
            return PlotStatus.WITHDRAWN;
        default:
            return PlotStatus.UNKNOWN;
        }
    }
    
    public static class BagStandplaatsImpl extends AbstractOdEntity implements BagStaticCaravanPlot {
        private Long id;
        private NLAddress address;
        private PlotStatus status;

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setAddress(NLAddress address) {
            this.address = address;
        }

        @Override
        public NLAddress getMainAddress() {
            return address;
        }

        @Override
        public String getStatusTag() {
            return getStatus().toString();
        }

        @Override
        public PlotStatus getStatus() {
            return status;
        }

        @Override
        public AddressableObjectStatus getAddressableStatus() {
            switch (getStatus()) {
            case ASSIGNED:
                return AddressableObjectStatus.ASSIGNED;
            case WITHDRAWN:
                return AddressableObjectStatus.WITHDRAWN;
            default:
                return AddressableObjectStatus.UNKNOWN;
            }
        }

        public void setStatus(PlotStatus status) {
            this.status = status;
        }
        
        @Override
        public Completeness getCompleteness() {
            return Completeness.Complete;
        }

        @Override
        public void setMatch(OdMatch<BagStaticCaravanPlot> match) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Match<? extends OsmEntity, ? extends OdEntity> getMatch() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean readyForImport() {
            return !(getStatus().equals(PlotStatus.WITHDRAWN));
        }
    }
}
