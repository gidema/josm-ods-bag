package org.openstreetmap.josm.plugins.ods.bag.external;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.bag.BagAddressDataImpl;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class ExternalBagAddress extends BagAddressDataImpl {
    private SimpleFeature feature;
	private Long identificatie;
	private String status;

	public ExternalBagAddress(SimpleFeature feature) {
		this.feature = feature;
	}

	public void init(MetaData metaData) throws BuildException {
	    identificatie = FeatureUtil.getLong(feature, "identificatie");
		setHuisnummer(FeatureUtil.getInteger(feature, "huisnummer"));
		setHuisletter(FeatureUtil.getString(feature, "huisletter"));
		setHuisnummerToevoeging(FeatureUtil.getString(feature, "toevoeging"));
		status = FeatureUtil.getString(feature, "status");
		setStreetName(FeatureUtil.getString(feature, "openbare_ruimte"));
		setCityName(FeatureUtil.getString(feature, "woonplaats"));
		setPostcode(FeatureUtil.getString(feature, "postcode"));
	}

    public Serializable getId() {
		return identificatie;
	}

	public Long getIdentificatie() {
		return identificatie;
	}

	public String getStatus() {
		return status;
	}
	
	public void buildTags(OsmPrimitive primitive) {
		primitive.put("addr:housenumber", getHouseNumber());
		primitive.put("addr:street", getStreetName());
		primitive.put("addr:postcode", getPostcode());
		primitive.put("addr:city", getCityName());
	}
	
    @Override
    public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(getStreetName()).append(" ");
	    sb.append(getHouseNumber()).append(" ");
        sb.append(getPostcode()).append(" ");
        sb.append(getCityName()).append(" ");	    
	    return sb.toString();
	}
}
