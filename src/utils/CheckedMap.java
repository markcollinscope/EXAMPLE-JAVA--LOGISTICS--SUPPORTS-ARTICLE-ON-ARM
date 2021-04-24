package utils;

import java.util.HashMap;
import java.util.Set;

public class CheckedMap<K,V> {
    private HashMap<K,V> myMap;

    private void checkForDuplicates(K key)
    throws DuplicateKeyException
    {
       if (myMap.containsKey(key))
       {
          throw new DuplicateKeyException("duplicate key found -");
       }
    }

    public CheckedMap() 
    {
        myMap = new HashMap<K,V>();
    }

    public void put(K key, V value)
    throws DuplicateKeyException
    {
        checkForDuplicates(key);
        myMap.put(key,value);
    }

    public V get(K key)
    throws UnknownKeyException
    {
        V value = myMap.get(key);
        if (value == null) 
            throw new UnknownKeyException("unknown key found");
        
        return value;
    }

    public Set<K> keySet()
    {
        return myMap.keySet();
    }

    public Boolean containsKey(K key)
    {
        return myMap.containsKey(key);
    }
}
