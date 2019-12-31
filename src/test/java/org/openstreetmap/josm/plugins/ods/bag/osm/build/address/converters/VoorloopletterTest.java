package org.openstreetmap.josm.plugins.ods.bag.osm.build.address.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConverter;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms.*;

class VoorloopletterTest {
    AddressConverter converter;

    @BeforeEach
    public void setup() {
        converter = new Voorloopletter();
    }

    @Test
    public void ignoreGenericTest() {
        OdAddress address = new BagOdAddress();
        address.setStreetName("Straat");
        address.setFullHouseNumber("12");
        address.setPostcode("1234AB");
        address.setCityName("Stad");

        assertFalse(converter.isApplicable(address));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void pekelaTest() {
        OdAddress address = new BagOdAddress();
        address.setStreetName("Dominee Sicco Tjadenstraat C");
        address.setFullHouseNumber("52");
        address.setPostcode("9663RC");
        address.setCityName("Nieuwe Pekela");

        BiConsumer<String, String> tagSetter = mock(BiConsumer.class);

        assertTrue(converter.isApplicable(address));

        converter.bagToOsm(address, tagSetter);

        verify(tagSetter).accept(ADDR_STREET, "Dominee Sicco Tjadenstraat");
        verify(tagSetter).accept(ADDR_HOUSENUMBER, "C52");
        verify(tagSetter).accept(ADDR_POSTCODE, "9663RC");
        verify(tagSetter).accept(ADDR_CITY, "Nieuwe Pekela");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void pekelaNormalTest() {
        OdAddress address = new BagOdAddress();
        address.setStreetName("Eigenhaardstraat");
        address.setFullHouseNumber("4");
        address.setPostcode("9663RH");
        address.setCityName("Nieuwe Pekela");

        assertFalse(converter.isApplicable(address));
    }

    @Test
    public void notationTest() {
        // Make sure all postcodes entered are valid.
        for (String pp4 : Voorloopletter.applicablePostcodes4) {
            assertTrue(
                    pp4.matches("[1-9][0-9]{3}"),
                    "Postcodes in this set must contain only the first four numbers."
            );
        }

        for (String pp6 : Voorloopletter.applicablePostcodes6) {
            assertTrue(
                    pp6.matches("[1-9][0-9]{3}[A-Z]{2}"),
                    "Postcodes in this set must be valid Dutch postcodes."
            );
        }
    }
}