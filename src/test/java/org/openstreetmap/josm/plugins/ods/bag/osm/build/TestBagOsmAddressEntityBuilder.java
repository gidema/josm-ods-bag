package org.openstreetmap.josm.plugins.ods.bag.osm.build;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

public class TestBagOsmAddressEntityBuilder {

    @Test
    public void test1() {
        Matcher matcher = BagOsmAddressEntityBuilder.houseNumberPattern.matcher("147-H");
        assertAll(() -> assertTrue(matcher.matches(), "No macht found"),
                () -> assertEquals("", matcher.group(1), "Prefix differs"),
                () -> assertEquals("147", matcher.group(2), "Number differs"),
                () -> assertEquals("", matcher.group(3), "House letter differs"),
                () -> assertEquals("H", matcher.group(6), "Housenumber extra differs"));
    }
}
