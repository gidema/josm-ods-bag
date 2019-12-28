package org.openstreetmap.josm.plugins.ods.bag.osm.build.address;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openstreetmap.josm.plugins.ods.bag.entity.BagOdAddress;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms.*;

class AddressConversionAlgorithmsTest {
    @Test
    @SuppressWarnings("unchecked")
    public void bagToOsmSimpleAddressTest() {
        OdAddress address = new BagOdAddress();
        address.setStreetName("Straat");
        address.setFullHouseNumber("12");
        address.setPostcode("1234AB");
        address.setCityName("Stad");

        BiConsumer<String, String> tagSetter = mock(BiConsumer.class);

        AddressConversionAlgorithms.bagToOsm(address, tagSetter);

        verify(tagSetter).accept(ADDR_STREET, "Straat");
        verify(tagSetter).accept(ADDR_HOUSENUMBER, "12");
        verify(tagSetter).accept(ADDR_POSTCODE, "1234AB");
        verify(tagSetter).accept(ADDR_CITY, "Stad");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void bagToOsmNullAddressTest() {
        BiConsumer<String, String> tagSetter = mock(BiConsumer.class);

        // Treat null address as no-op.
        AddressConversionAlgorithms.bagToOsm(null, tagSetter);

        verify(tagSetter, never()).accept(anyString(), anyString());
    }

    @Test
    public void bagToOsmNullSetterTest() {
        OdAddress address = new BagOdAddress();

        assertThrows(
                IllegalArgumentException.class,
                () -> AddressConversionAlgorithms.bagToOsm(address, null)
        );
    }
}