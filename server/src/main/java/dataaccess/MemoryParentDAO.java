package dataaccess;

import java.util.Collection;
import java.util.Map;

public class MemoryParentDAO<K, V extends model.Identifier<K>> implements DataAccessInterface<K, V>{
    public Map<K, V> db;
//    private static Map<K, V> staticdb;

    public V get(K id) {
        return db.get(id);
    }

    public void add(V value) {
        db.put(value.getId(), value);
    }

    public void update(K key, V value) throws DataAccessException {
        if ( !db.containsKey(key) ) throw new DataAccessException("Key: " + key + " isn't found in the database, unable to update it. ");
        if ( db.containsKey(value.getId()) && key != value.getId()) throw new DataAccessException("Unable to update value: " + value + " to key: " + value.getId() + " because that key already exists. ");
        remove(key);
        add(value);
    }

    public void remove(K key) {
        db.remove(key);
    }

    public Collection<V> getAll() {
        return db.values();
    }

    public void clear() {
        db.clear();
    }
}
