package org.openstreetmap.josm.plugins.ods.bag.modifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_Address;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_HouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.od.OdAddressModifier;

public class NL_PrefixedHouseNumberAddressModifierTest {
    OdAddressModifier<NL_Address> modifier;

    @BeforeEach
    public void setup() {
        modifier = new NL_PrefixedHouseNumberAddressModifier();
    }

    @Test
    public void ignoreGenericTest() {
        NL_HouseNumber hnr = new NL_HouseNumberImpl("12");
        NL_Address address = new NL_Address();
        address.setStreetName("Straat");
        address.setHouseNumber(hnr);
        address.setPostcode("1234AB");
        address.setCityName("Stad");

        modifier.modify(address);
        assertEquals(hnr, address.getHouseNumber());
        assertEquals("Straat", address.getStreetName());
    }

    @Test
    public void pekelaTest() {
        NL_HouseNumber hnr = new NL_HouseNumberImpl("52");
        NL_Address address = new NL_Address();
        address.setStreetName("Dominee Sicco Tjadenstraat C");
        address.setHouseNumber(hnr);
        address.setPostcode("9663RC");
        address.setCityName("Nieuwe Pekela");

        modifier.modify(address);
        assertEquals("C52", address.getHouseNumber().getFullHouseNumber());
        assertEquals("Dominee Sicco Tjadenstraat", address.getStreetName());
    }

    @Test
    public void notationTest() {
        // Make sure all postcodes entered are valid.
        for (String pp4 : NL_PrefixedHouseNumberAddressModifier.applicablePostcodes4) {
            assertTrue(
                    pp4.matches("[1-9][0-9]{3}"),
                    "Postcodes in this set must contain only the first four numbers."
            );
        }

        for (String pp6 : NL_PrefixedHouseNumberAddressModifier.applicablePostcodes6) {
            assertTrue(
                    pp6.matches("[1-9][0-9]{3}[A-Z]{2}"),
                    "Postcodes in this set must be valid Dutch postcodes."
            );
        }
    }
}

