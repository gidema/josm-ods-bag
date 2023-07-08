package org.openstreetmap.josm.plugins.ods.bag.factories;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.bag.entity.BagMooringParcel;
import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.storage.BagMooringParcelStore;
import org.openstreetmap.josm.plugins.ods.bag.pdok.BagPdok;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OdEntityFactory;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.AbstractOdEntity;
import org.openstreetmap.josm.plugins.ods.entities.opendata.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Match;
import org.openstreetmap.josm.plugins.ods.matching.OdMatch;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.WfsFeature;

public class BagMooringParcelFactory implements OdEntityFactory {
    private static QName typeName = new QName(BagPdok.NS_BAG, "ligplaats");
    private BagMooringParcelStore ligplaatsStore;

    public BagMooringParcelFactory(OdsContext context) {
        super();
        this.ligplaatsStore = context.getComponent(BagMooringParcelStore.class);
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
        ligplaats.setLigplaatsId(Long.valueOf(id));
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

    private static EntityStatus parseStatus(String status) {
        switch (status) {
        case "Plaats aangewezen":
            return EntityStatus.IN_USE;
        case "Plaats ingetrokken":
            return EntityStatus.REMOVED;
        default:
            return EntityStatus.UNKNOWN;
        }
    }
    
    public static class BagLigplaatsImpl extends AbstractOdEntity implements BagMooringParcel {
        private Long ligplaatsId;
        private NLAddress address;
        private OdMatch<BagMooringParcel> match;

        public void setAddress(NLAddress address) {
            this.address = address;
        }

        @Override
        public Long getLigplaatsId() {
            return ligplaatsId;
        }

        public void setLigplaatsId(Long ligplaatsId) {
            this.ligplaatsId = ligplaatsId;
        }

        @Override
        public NLAddress getAddress() {
            return address;
        }

        @Override
        public Completeness getCompleteness() {
            return Completeness.Complete;
        }

        @Override
        public Match<? extends OsmEntity, ? extends OdEntity> getMatch() {
            return null;
        }

        @Override
        public void setMatch(OdMatch<BagMooringParcel> match) {
            this.match = match;
        }

        @Override
        public boolean readyForImport() {
            return !(getStatus().equals(EntityStatus.REMOVED));
        }
    }
}
