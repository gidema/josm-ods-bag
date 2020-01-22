package org.openstreetmap.josm.plugins.ods.bag.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_Address;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;

public class NL_GenericAddressFactoryTest {
    private static SimpleFeatureType verblijfsobjectType;

    @BeforeAll
    private static void createFeatureTypes() {
        verblijfsobjectType = createVerblijfsobjectType();
    }

    private static SimpleFeatureType createVerblijfsobjectType() {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("verblijfsobject");
        builder.add("geometrie", Object.class);
        builder.add("gid", BigInteger.class);
        builder.add("identificatie", String.class);
        builder.add("oppervlakte", Integer.class);
        builder.add("status", String.class);
        builder.add("gebruiksdoel", String.class);
        builder.add("openbare_ruimte", String.class);
        builder.add("huisnummer", String.class);
        builder.add("huisletter", String.class);
        builder.add("toevoeging", String.class);
        builder.add("postcode", String.class);
        builder.add("woonplaats", String.class);
        builder.add("bouwjaar", String.class);
        builder.add("pandidentificatie", String.class);
        builder.add("pandstatus", String.class);
        builder.add("rdf_seealso", String.class);
        return builder.buildFeatureType();
    }

    @SuppressWarnings("static-method")
    @Test
    void createAddressTest() {
        SimpleFeature vbo = SimpleFeatureBuilder.build(verblijfsobjectType,
                new Object[] {null, 0, "0", 0, "dummy", "woonfunctie", "Straat",
                        "12", "A", "hoog", "1234AB", "Plaats", "2000",
                        null, null, null}, "");
        NL_GenericAddressFactory factory = new NL_GenericAddressFactory();
        DownloadResponse response = Mockito.mock(DownloadResponse.class);
        NL_Address address = factory.create(vbo, response);
        assertEquals("Straat", address.getStreetName());
        assertEquals("1234AB", address.getPostcode());
        assertEquals("Plaats", address.getCityName());
        assertEquals((Integer)12, address.getHouseNumber().getMainHouseNumber());
        assertEquals((Character)'A', address.getHouseNumber().getHouseLetter());
        assertEquals("hoog", address.getHouseNumber().getHouseNumberExtra());
        assertEquals("12A-hoog", address.getHouseNumber().getFullHouseNumber());
    }
}
