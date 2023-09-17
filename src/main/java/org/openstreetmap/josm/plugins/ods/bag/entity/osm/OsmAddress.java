package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import org.openstreetmap.josm.plugins.ods.bag.entity.NLAddress;
import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.domains.places.OsmCity;

public interface OsmAddress extends NLAddress {

    public OsmStreet getStreet();

    public OsmCity getCity();

    void setCityName(String cityName);

    void setStreetName(String streetName);

    void setPostcode(String postcode);

    void setHouseNumber(NlHouseNumber houseNumber);
}
