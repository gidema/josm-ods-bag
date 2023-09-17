package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.OsmEntityStore;

/**
 * Store address nodes created from osm primitives.
 * This store has .. indexes:
 *   primitiveIndex. This is also the primary index and indexes the unique primitiveId.
 *   geoIndex. The geographical index on the addressNodes
 *   zipHousenrIndex. An index on the zipcode and the numerical part of the
 *
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class OsmAddressNodeStore extends OsmEntityStore<OsmAddressNode> {
    private final Index<OsmAddressNode> pcHnrIndex = new IndexImpl<>(OsmAddressNode.class, PostcodeHousenumber::of);
    private final GeoIndex<OsmAddressNode> geoIndex = new GeoIndexImpl<>();

    public OsmAddressNodeStore() {
    }

    public Index<OsmAddressNode> getPcHnrIndex() {
        return pcHnrIndex;
    }

    
//    @Override
//    public GeoIndex<OsmAddressNode> getGeoIndex() {
//        return geoIndex;
//    }
    
    @Override
    public void onAdd(OsmAddressNode entity) {
        pcHnrIndex.insert(entity);
    }

    @Override
    public void onRemove(OsmAddressNode entity) {
        pcHnrIndex.remove(entity);
    }

    @Override
    public void beforeClear() {
        pcHnrIndex.clear();
    }

    public static class PostcodeHousenumber {
        private final String postcode;
        private final Integer houseNumber;

        public PostcodeHousenumber(String postcode, Integer houseNumber) {
            super();
            this.postcode = postcode;
            this.houseNumber = houseNumber;
        }

        public String getPostcode() {
            return postcode;
        }
        public Integer getHouseNumber() {
            return houseNumber;
        }
        
        public static PostcodeHousenumber of(OsmAddressNode an) {
            return new PostcodeHousenumber(an.getAddress().getPostcode(), an.getAddress().getHouseNumber().getHouseNumber());
        }

        @Override
        public int hashCode() {
            return Objects.hash(houseNumber, postcode);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PostcodeHousenumber other = (PostcodeHousenumber) obj;
            return Objects.equals(houseNumber, other.houseNumber)
                    && Objects.equals(postcode, other.postcode);
        }

        @Override
        public String toString() {
            return "PostcodeHousenumber " + postcode
                    + " " + houseNumber;
        }
    }
}
