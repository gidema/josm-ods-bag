package org.openstreetmap.josm.plugins.ods.bag.osm.build.address.converters;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConverter;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.function.BiConsumer;

import static org.openstreetmap.josm.plugins.ods.bag.osm.build.address.AddressConversionAlgorithms.*;

/**
 * Generic conversion algorithm that should be the default fall-back.
 */
public class Generiek implements AddressConverter {
    /**
     * Check if this converter can be used for this address. As this is the catch-all converter, {@code true} is always
     * returned.
     *
     * @param address BAG address.
     * @return True, always.
     */
    @Override
    public boolean isApplicable(OdAddress address) {
        // Catch-all. Works for all Dutch addresses.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bagToOsm(OdAddress address, BiConsumer<String, String> tagSetter) {
        if (address.getStreetName() != null) {
            tagSetter.accept(ADDR_STREET, address.getStreetName());
        }
        if (address.getFullHouseNumber() != null
                && !address.getFullHouseNumber().isEmpty()
                // Bug in formatHouseNumber causes a literal string "null" to be returned if there is no housenumber.
                && !address.getFullHouseNumber().equals("null")) {
            tagSetter.accept(ADDR_HOUSENUMBER, address.getFullHouseNumber());
        }
        if (address.getPostcode() != null) {
            tagSetter.accept(ADDR_POSTCODE, address.getPostcode());
        }
        if (address.getCityName() != null) {
            tagSetter.accept(ADDR_CITY, address.getCityName());
        }
    }
}
