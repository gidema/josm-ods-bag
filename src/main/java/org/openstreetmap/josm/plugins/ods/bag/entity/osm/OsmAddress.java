package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.Address;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;

public interface OsmAddress extends Address {

    public OsmStreet getStreet();

    public OsmCity getCity();

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(NlHouseNumber houseNumber);
}
