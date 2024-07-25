package dataaccess;

import model.Identifier;

import java.util.Collection;

public interface DataAccessInterface<K, V extends Identifier<K>> {
    V get(K id);
    void add(V value);
    void update(K key, V value) throws DataAccessException;
    void remove(K key);
    Collection<V> getAll();
    void clear();
}
