package org.openstreetmap.josm.plugins.ods.bag.osm.build.address;

import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.function.BiConsumer;

/**
 * Converts BAG addresses to OSM address tags.
 */
public interface AddressConverter {
    /**
     * Check if this converter can be used for this address.
     *
     * @param address BAG address.
     * @return True if this class can perform the conversion.
     */
    boolean isApplicable(OdAddress address);

    /**
     * Perform the conversion.
     *
     * @param address   BAG address.
     * @param tagSetter Will be called for each tag to set.
     */
    void bagToOsm(OdAddress address, BiConsumer<String, String> tagSetter);
}
