package org.openstreetmap.josm.plugins.ods.bag.relations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Relation<T extends Relation.Tuple> implements Iterable<T> {
    private final Set<T> set = new HashSet<>();

    public void add(T tuple) {
        set.add(tuple);
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    public static interface Tuple {
        // Parent of all Tuples
    }
}
