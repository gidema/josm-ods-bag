package org.openstreetmap.josm.plugins.ods.bag.entity.osm;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.openstreetmap.josm.plugins.ods.entities.storage.AbstractGeoEntityStore;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.GeoIndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.Index;
import org.openstreetmap.josm.plugins.ods.entities.storage.IndexImpl;
import org.openstreetmap.josm.plugins.ods.entities.storage.PrimaryIndex;
import org.openstreetmap.josm.plugins.ods.entities.storage.UniqueIndexImpl;

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
public class OsmAddressNodeStore extends AbstractGeoEntityStore<OsmAddressNode> {
    private final PrimaryIndex<OsmAddressNode> primitiveIndex = new UniqueIndexImpl<OsmAddressNode>(OsmAddressNode::getPrimitiveId);
    private final Index<OsmAddressNode> pcHnrIndex = new IndexImpl<OsmAddressNode>(OsmAddressNode.class, PostcodeHousenumber::of);
    private final GeoIndex<OsmAddressNode> geoIndex = new GeoIndexImpl<>(OsmAddressNode.class, "geometry");
    private final List<Index<OsmAddressNode>> allIndexes = Arrays.asList(primitiveIndex, pcHnrIndex, geoIndex);

    public OsmAddressNodeStore() {
    }

    @Override
    public PrimaryIndex<OsmAddressNode> getPrimaryIndex() {
        return primitiveIndex;
    }

    public Index<OsmAddressNode> getPcHnrIndex() {
        return pcHnrIndex;
    }

    @Override
    public GeoIndex<OsmAddressNode> getGeoIndex() {
        return geoIndex;
    }

    @Override
    public List<Index<OsmAddressNode>> getAllIndexes() {
        return allIndexes;
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
