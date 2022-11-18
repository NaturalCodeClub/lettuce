package org.bukkit.craftbukkit.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class HashTreeSet<V> implements Set<V> {

    private HashSet<V> hash = new HashSet<V>();
    private TreeSet<V> tree = new TreeSet<V>();

    @Override
    public synchronized int size() {
        return hash.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return hash.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return hash.contains(o);
    }

    @Override
    public synchronized Iterator<V> iterator() {
        return new Iterator<V>() {

            private Iterator<V> it = tree.iterator();
            private V last;

            @Override
            public synchronized boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public synchronized V next() {
                return last = it.next();
            }

            @Override
            public synchronized void remove() {
                if (last == null) {
                    throw new IllegalStateException();
                }
                it.remove();
                hash.remove(last);
                last = null;
            }
        };
    }

    @Override
    public synchronized Object[] toArray() {
        return hash.toArray();
    }

    @Override
    public synchronized Object[] toArray(Object[] a) {
        return hash.toArray(a);
    }

    @Override
    public synchronized boolean add(V e) {
        hash.add(e);
        return tree.add(e);
    }

    @Override
    public synchronized boolean remove(Object o) {
        hash.remove(o);
        return tree.remove(o);
    }

    @Override
    public synchronized boolean containsAll(Collection c) {
        return hash.containsAll(c);
    }

    @Override
    public synchronized boolean addAll(Collection c) {
        tree.addAll(c);
        return hash.addAll(c);
    }

    @Override
    public synchronized boolean retainAll(Collection c) {
        tree.retainAll(c);
        return hash.retainAll(c);
    }

    @Override
    public synchronized boolean removeAll(Collection c) {
        tree.removeAll(c);
        return hash.removeAll(c);
    }

    @Override
    public synchronized void clear() {
        hash.clear();
        tree.clear();
    }

    public synchronized V first() {
        return tree.first();
    }

}
