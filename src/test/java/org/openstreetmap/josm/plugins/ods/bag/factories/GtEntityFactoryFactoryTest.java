package org.openstreetmap.josm.plugins.ods.bag.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geotools.data.complex.feature.type.Types;
import org.junit.jupiter.api.Test;
import org.opengis.feature.type.Name;
import org.openstreetmap.josm.plugins.ods.bag.entity.NL_Address;
import org.openstreetmap.josm.plugins.ods.geotools.GtEntityFactory;
import org.openstreetmap.josm.plugins.ods.od.GtEntityFactoryFactory;

public class GtEntityFactoryFactoryTest {
    @Test
    void create() {
        Name name = Types.typeName("", "verblijfsobject");
        GtEntityFactory<NL_Address> addressFactory = GtEntityFactoryFactory.create(name, NL_Address.class);
        assertNotNull(addressFactory);
        assertEquals(addressFactory.getClass(), NL_GenericAddressFactory.class);
    }
}
