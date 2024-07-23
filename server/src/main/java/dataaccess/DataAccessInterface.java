package dataaccess;

import model.Identifier;

import java.util.Collection;

public interface DataAccessInterface<K, V extends Identifier<K>> {
    public V get(K id);
    public void add(V value);
    public void update(K key, V value) throws DataAccessException;
    public void remove(K key);
    public Collection<V> getAll();
    public void clear();
}
