package gg.m2ke4u.skylight;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentCastableNonLinkedHashSet<T> extends LinkedHashSet<T>{
    private final Set<T> baking = ConcurrentHashMap.newKeySet();

    @Override
    public int size() {
        return this.baking.size();
    }

    @Override
    public boolean isEmpty() {
        return this.baking.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.baking.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.baking.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.baking.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return this.baking.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return this.baking.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return this.baking.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.baking.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return this.baking.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.baking.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.baking.removeAll(c);
    }

    @Override
    public void clear() {
        this.baking.clear();
    }

    @Override
    public boolean equals(Object o) {
        return this.baking.equals(o);
    }

    @Override
    public int hashCode() {
        return this.baking.hashCode();
    }
}