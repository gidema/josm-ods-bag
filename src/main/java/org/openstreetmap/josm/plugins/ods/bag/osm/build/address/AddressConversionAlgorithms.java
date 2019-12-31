package org.openstreetmap.josm.plugins.ods.bag.osm.build.address;

import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.converters.Generiek;
import org.openstreetmap.josm.plugins.ods.bag.osm.build.address.converters.Voorloopletter;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddress;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Convert an address from the BAG to OSM address tags. This is usually straight forward, but to handle exceptions
 * (e.g., where the BAG data is wrongly formatted), this class can be extended by implementing {@link AddressConverter}
 * and adding the converter to the service-loader.
 */
public class AddressConversionAlgorithms {
    public static final String ADDR_STREET = "addr:street";
    public static final String ADDR_HOUSENUMBER = "addr:housenumber";
    public static final String ADDR_POSTCODE = "addr:postcode";
    public static final String ADDR_CITY = "addr:city";

    static List<AddressConverter> converters;

    static {
        // The order of converters matters. The first applicable converter for an address will be called.
        converters = Arrays.asList(

                new Voorloopletter(),

                // Catch-all. This one should always be the last in the list.
                new Generiek()

        );
    }

    /**
     * Convert a BAG address to OSM tags.
     *
     * @param address   BAG address. May be {@code null} (no tags will be set in that case).
     * @param tagSetter Will be called for each tag to set.
     */
    public static void bagToOsm(OdAddress address, BiConsumer<String, String> tagSetter) {
        if (address == null) return;
        if (tagSetter == null) throw new IllegalArgumentException("No tagSetter provided.");

        converters.stream()
                .filter(converter -> converter.isApplicable(address))
                .findFirst()
                // Not possible if Generiek is present in the list.
                .orElseThrow(RuntimeException::new)
                .bagToOsm(address, tagSetter);
    }
}
