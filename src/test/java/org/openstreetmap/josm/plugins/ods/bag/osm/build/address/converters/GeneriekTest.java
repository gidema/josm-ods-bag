package org.openstreetmap.josm.plugins.ods.bag.osm.build.address.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConverter;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms.*;
import static org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms.ADDR_CITY;

class GeneriekTest {
    AddressConverter converter;

    @BeforeEach
    public void setup() {
        converter = new Generiek();
    }

    @Test
    public void isApplicableTest() {
        assertTrue(converter.isApplicable(mock(OdAddress.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bagToOsmTest() {
        OdAddress address = new BagOdAddress();
        address.setStreetName("Straat");
        address.setFullHouseNumber("12");
        address.setPostcode("1234AB");
        address.setCityName("Stad");

        BiConsumer<String, String> tagSetter = mock(BiConsumer.class);

        converter.bagToOsm(address, tagSetter);

        verify(tagSetter).accept(ADDR_STREET, "Straat");
        verify(tagSetter).accept(ADDR_HOUSENUMBER, "12");
        verify(tagSetter).accept(ADDR_POSTCODE, "1234AB");
        verify(tagSetter).accept(ADDR_CITY, "Stad");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bagToOsmHandleNullsTest() {
        OdAddress address = new BagOdAddress();

        BiConsumer<String, String> tagSetter = mock(BiConsumer.class);

        converter.bagToOsm(address, tagSetter);

        verify(tagSetter, never()).accept(anyString(), anyString());
    }
}