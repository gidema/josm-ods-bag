package org.openstreetmap.josm.plugins.ods.bag;

import java.io.Serializable;

import org.opengis.feature.simple.SimpleFeature;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.external.ExternalAddress;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class BagAddress extends ExternalAddress {
	private Long identificatie;
	private Integer huisnummer;
	private String huisletter;
	private String huisnummertoevoeging;
	private String postcode;
	private String status;
	private String gebruiksdoel;
	private Long gerelateerdPand;
	private String openbareRuimte;
	private String woonplaats;
	private String houseNumber;

	public BagAddress(SimpleFeature feature) {
		super(feature);
	}

	public void init(MetaData metaData) throws BuildException {
		SimpleFeature feature = getFeature();
		identificatie = ((Double) feature.getProperty("identificatie").getValue()).longValue();
		huisnummer = ((Double) feature.getProperty("huisnummer").getValue()).intValue();
		huisletter = (String) feature.getProperty("huisletter").getValue();
		huisnummertoevoeging = (String) feature.getProperty("toevoeging").getValue();
		status = (String) feature.getProperty("status").getValue();
		gebruiksdoel = (String) feature.getProperty("gebruiksdoel").getValue();
		gerelateerdPand = ((Double) feature.getProperty("pandidentificatie").getValue()).longValue();
		openbareRuimte = (String) feature.getProperty("openbare_ruimte").getValue();
		woonplaats = (String) feature.getProperty("woonplaats").getValue();
		postcode = (String) feature.getProperty("postcode").getValue();
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
	
	public String getGebruiksdoel() {
		return gebruiksdoel;
	}

	public Serializable getBuildingRef() {
		return gerelateerdPand;
	}

	public String getOpenbareRuimte() {
		return openbareRuimte;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	public String getPostcode() {
		return postcode;
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
}
