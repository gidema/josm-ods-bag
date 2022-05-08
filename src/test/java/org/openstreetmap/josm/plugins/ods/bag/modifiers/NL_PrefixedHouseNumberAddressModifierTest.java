package org.openstreetmap.josm.plugins.ods.bag.modifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlAddressImpl;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;
import org.openstreetmap.josm.plugins.ods.entities.EntityModifier;

public class NL_PrefixedHouseNumberAddressModifierTest {
    EntityModifier<NlAddressImpl> modifier;

    @BeforeEach
    public void setup() {
        modifier = new NlPrefixedHouseNumberAddressModifier();
    }

    @Test
    public void ignoreGenericTest() {
        NlHouseNumber hnr = new NlHouseNumberImpl(12, null, null);
        NlAddressImpl address = new NlAddressImpl();
        address.setStreetName("Straat");
        address.setHouseNumber(hnr);
        address.setPostcode("1234AB");
        address.setCityName("Stad");

        assertFalse(modifier.isApplicable(address));
    }

    @Test
    public void pekelaTest() {
        NlHouseNumber hnr = new NlHouseNumberImpl(52, null, null);
        NlAddressImpl address = new NlAddressImpl();
        address.setStreetName("Dominee Sicco Tjadenstraat C");
        address.setHouseNumber(hnr);
        address.setPostcode("9663RC");
        address.setCityName("Nieuwe Pekela");

        assertTrue(modifier.isApplicable(address));
        modifier.modify(address);
        assertEquals("C52", address.getHouseNumber().getFullHouseNumber());
        assertEquals("Dominee Sicco Tjadenstraat", address.getStreetName());
    }

    @Test
    public void notationTest() {
        // Make sure all postcodes entered are valid.
        for (String pp4 : NlPrefixedHouseNumberAddressModifier.applicablePostcodes4) {
            assertTrue(
                    pp4.matches("[1-9][0-9]{3}"),
                    "Postcodes in this set must contain only the first four numbers."
            );
        }

        for (String pp6 : NlPrefixedHouseNumberAddressModifier.applicablePostcodes6) {
            assertTrue(
                    pp6.matches("[1-9][0-9]{3}[A-Z]{2}"),
                    "Postcodes in this set must be valid Dutch postcodes."
            );
        }
    }
}

