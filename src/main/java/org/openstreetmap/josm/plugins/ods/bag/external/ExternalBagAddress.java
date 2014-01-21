package org.openstreetmap.josm.plugins.ods.bag.external;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Street;
import org.openstreetmap.josm.plugins.ods.entities.external.FeatureUtil;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class ExternalBagAddress implements Address {
    private SimpleFeature feature;
	private Long identificatie;
	private Integer huisnummer;
	private String huisletter;
	private String huisnummertoevoeging;
	private String status;
	private String openbareRuimte;
	private String woonplaats;

    private String postcode;
    private String houseNumber;
    private Street street;
    private City city;

	public ExternalBagAddress(SimpleFeature feature) {
		this.feature = feature;;
	}

	public void init(MetaData metaData) throws BuildException {
	    identificatie = FeatureUtil.getLong(feature, "identificatie");
		huisnummer = FeatureUtil.getInteger(feature, "huisnummer");
		huisletter = FeatureUtil.getString(feature, "huisletter");
		huisnummertoevoeging = FeatureUtil.getString(feature, "toevoeging");
		status = FeatureUtil.getString(feature, "status");
		openbareRuimte = FeatureUtil.getString(feature, "openbare_ruimte");
		woonplaats = FeatureUtil.getString(feature, "woonplaats");
		postcode = FeatureUtil.getString(feature, "postcode");
	}

	public Serializable getId() {
		return identificatie;
	}

	public Long getIdentificatie() {
		return identificatie;
	}

	public Integer getHuisnummer() {
		return huisnummer;
	}

	public String getHuisLetter() {
		return huisletter;
	}

	public String getHuisNummerToevoeging() {
		return huisnummertoevoeging;
	}

	public String getStatus() {
		return status;
	}
	
	public String getOpenbareRuimte() {
		return openbareRuimte;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	@Override
	public String getHouseNumber() {
		if (houseNumber == null) {
			StringBuilder sb = new StringBuilder(10);
			sb.append(getHuisnummer());
			if (getHuisLetter() != null) {
				sb.append(getHuisLetter());
			}
			if (getHuisNummerToevoeging() != null) {
				sb.append('-').append(getHuisNummerToevoeging());
			}
			houseNumber = sb.toString();
		}
		return houseNumber;
	}

	@Override
	public String getStreetName() {
		return getOpenbareRuimte();
	}

	@Override
	public String getPlaceName() {
		return getWoonplaats();
	}


	@Override
	public City getCity() {
		return city;
	}

	@Override
	public Street getStreet() {
		return street;
	}

	@Override
	public String getPostcode() {
		return postcode;
	}

	@Override
	public String getHouseName() {
		return null;
	}

	@Override
	public void setStreet(Street street) {
		this.street = street;
	}
	
	public void buildTags(OsmPrimitive primitive) {
		primitive.put("addr:housenumber", getHouseNumber());
		primitive.put("addr:street", getStreetName());
		primitive.put("addr:postcode", getPostcode());
		primitive.put("addr:city", getPlaceName());
	}
	
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append(getStreetName()).append(" ");
	    sb.append(getHouseNumber()).append(" ");
        sb.append(getPostcode()).append(" ");
        sb.append(getPlaceName()).append(" ");	    
	    return sb.toString();
	}
}
