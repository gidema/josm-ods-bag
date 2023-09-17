package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.AddressableObjectStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringPlot;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.PlotStatus;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringPlotStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.mapping.Mapping;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;
import org.openstreetmap.josm.plugins.ods.update.UpdateTaskType;

public class BagMooringPlotFactory implements OdEntityFactory {
    private static QName typeName = new QName(BagPdok.NS_BAG, "ligplaats");
    private BagMooringPlotStore ligplaatsStore;

    public BagMooringPlotFactory(OdsContext context) {
        super();
        this.ligplaatsStore = context.getComponent(BagMooringPlotStore.class);
    }

    @Override
    public boolean appliesTo(QName featureType) {
        // TODO Auto-generated method stub
        return featureType.equals(typeName);
    }

    @Override
    public void process(WfsFeature feature, DownloadResponse response) {
        BagLigplaatsImpl ligplaats = new BagLigplaatsImpl();
        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
        if (date != null) {
            ligplaats.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        }
        String id = FeatureUtil.getString(feature, "identificatie");
        ligplaats.setId(Long.valueOf(id));
//        LocalDate date = response.getRequest().getDownloadTime().toLocalDate();
//        if (date != null) {
//            entity.setSourceDate(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
//        }
        ligplaats.setSource("BAG");
        ligplaats.setStatus(parseStatus(FeatureUtil.getString(feature, "status")));
        ligplaats.setGeometry(feature.getGeometry());
        NlHouseNumber houseNumber = new NlHouseNumberImpl(
            FeatureUtil.getInteger(feature, "huisnummer"),
            FeatureUtil.getCharacter(feature, "huisletter"),
            FeatureUtil.getString(feature, "toevoeging"));
        NlAddressImpl address = new NlAddressImpl();
        address.setHouseNumber(houseNumber);
        address.setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
        address.setCityName(FeatureUtil.getString(feature, "woonplaats"));
        address.setPostcode(FeatureUtil.getString(feature, "postcode"));
        ligplaats.setAddress(address);
        ligplaatsStore.add(ligplaats);
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
    
    public static class BagLigplaatsImpl extends AbstractOdEntity implements BagMooringPlot {
        private Long id;
        private NLAddress address;
        private PlotStatus status;
        private Mapping<? extends OsmEntity, ? extends OdEntity> mapping;

        public void setAddress(NLAddress address) {
            this.address = address;
        }

        @Override
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public NLAddress getMainAddress() {
            return address;
        }

        @Override
        public String getStatusTag() {
            return status.toString();
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
        public Mapping<? extends OsmEntity, ? extends OdEntity> getMapping() {
            return null;
        }

        @Override
        public void setMapping(Mapping<? extends OsmEntity, ? extends OdEntity> mapping) {
            this.mapping = mapping;
        }

        @Override
        public boolean readyForImport() {
            return !(getStatus().equals(PlotStatus.WITHDRAWN));
        }

        @Override
        public UpdateTaskType getUpdateTaskType() {
            return UpdateTaskType.NONE;
        }
    }
}
