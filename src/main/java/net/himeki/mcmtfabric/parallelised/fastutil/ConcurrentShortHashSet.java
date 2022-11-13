package net.himeki.mcmtfabric.parallelised.fastutil;

import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSet;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentShortHashSet implements ShortSet {

    ConcurrentHashMap.KeySetView<Short, Boolean> backing = new ConcurrentHashMap<Short, Integer>().newKeySet();

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public ShortIterator iterator() {
        return new FastUtilHackUtil.WrappingShortIterator(backing.iterator());
    }

    @Override
    public ShortIterator shortIterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return backing.toArray();
    }

    @Override
    public <T> T[] toArray( T[] ts) {
        return (T[]) backing.toArray();
    }

    @Override
    public boolean add(Short aShort) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll( Collection<?> collection) {
        return backing.containsAll(collection);
    }

    @Override
    public boolean addAll( Collection<? extends Short> collection) {
        return backing.addAll(collection);
    }

    @Override
    public boolean removeAll( Collection<?> collection) {
        return backing.removeAll(collection);
    }

    @Override
    public boolean retainAll( Collection<?> collection) {
        return backing.retainAll(collection);
    }

    @Override
    public void clear() {
        backing.clear();

    }

    @Override
    public boolean add(short key) {
        return backing.add(key);
    }

    @Override
    public boolean rem(short key) {
        return false;
    }

    @Override
    public boolean contains(short key) {
        return backing.contains(key);
    }

    @Override
    public short[] toShortArray() {
        return new short[0];
    }

    @Override
    public short[] toShortArray(short[] a) {
        return new short[0];
    }

    @Override
    public short[] toArray(short[] a) {
        return new short[0];
    }

    @Override
    public boolean addAll(ShortCollection c) {
        return backing.addAll(c);
    }

    @Override
    public boolean containsAll(ShortCollection c) {
        return backing.containsAll(c);
    }

    @Override
    public boolean removeAll(ShortCollection c) {
        return backing.removeAll(c);
    }

    @Override
    public boolean retainAll(ShortCollection c) {
        return backing.retainAll(c);
    }

    @Override
    public boolean remove(short k) {
        return backing.remove(k);
    }
}
