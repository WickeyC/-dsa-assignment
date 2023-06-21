package adt;

public interface MapInterface<K, V> {
  /** Task: Adds a new entry to the map. If the given search
   *        key already exists in the dictionary, replaces the 
   *        corresponding value.
   *  @param key    an object search key of the new entry
   *  @param value  an object associated with the search key
   *  @return null if the new entry was added to the map 
   *          or the value that was associated with key if that value 
   *          was replaced */
  V put(K key, V value);

  /** Task: Retrieves the value associated with a given search key.
   *  @param key  an object search key of the entry to be retrieved
   *  @return the value that is associated with the search key 
   *          or null if no such object exists */
  V get(K key);

  /** Task: Removes a specific entry with the given search key from the map.
   *  @param key  an object search key of the entry to be removed
   *  @return the value that was associated with the search key 
   *          or null if no such object exists */
  V remove(K key);

  /** Task: Determines whether the map is empty (contains no key-value mappings).
   *  @return true if the map is is empty, or false if not */
  boolean isEmpty();

  /** Task: Gets the number of entries currently in the map.
   *  @return the number of entries (key-value mappings) in this map
   */
  int size();

  /** Task: Removes all of the mappings from this map (optional operation).
   * The map will be empty after this call returns.
   */
  void clear();

  /** Task: Determines whether the map contains a mapping for the specified key.
   * @param key key whose presence in this map is to be tested
   * @return true if this map contains a mapping for the specified key
   */
  boolean containsKey(K key);

  /** Task: Returns an iterable collection containing all the keys stored in this map.
   * @return an iterable collection containing all the keys stored in this map
  */
  Iterable<K> keys();

  /** Task: Returns an iterable collection containing all the values of entries 
   * stored in this map (with repetition if multiple keys map to the same value).
   * @return an iterable collection containing all the values of entries stored in this map
  */
  Iterable<V> values();

  /** Task: Returns an iterable collection containing all entries (key-value pairs)
   * stored in this map.
   * @return an iterable collection containing all the entries in this map
  */
  Iterable<Entry<K, V>> entries();
}
