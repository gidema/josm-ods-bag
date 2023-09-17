package org.openstreetmap.josm.plugins.ods.bag.sandbag;

import java.util.TreeSet;

import org.openstreetmap.josm.plugins.ods.bag.entity.NlHouseNumber;
import org.openstreetmap.josm.plugins.ods.bag.entity.impl.NlHouseNumberImpl;

public class TreeSetTest {
    public static void main(String... args) {
        var hnrMap = setup();
        var it = hnrMap.tailSet(new NlHouseNumberImpl(1, 'B', null), true).iterator();
        var hnr = it.next();
        hnr = it.next();
        int i=0;
    }
    
    public static TreeSet<NlHouseNumber> setup() {
        var hnrMap = new TreeSet<>(NlHouseNumber.DEFAULT_COMPARATOR);
        hnrMap.add(new NlHouseNumberImpl(1, 'A', null));
        hnrMap.add(new NlHouseNumberImpl(1, 'B', "X"));
        hnrMap.add(new NlHouseNumberImpl(1, 'B', "Y"));
        return hnrMap;
    }
}
