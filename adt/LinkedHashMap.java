package adt;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedHashMap<K, V> implements MapInterface<K, V>, Serializable {
  private static final int DEFAULT_SIZE = 101; // default size of hash table - must be prime
  private static final double MAX_LOAD_FACTOR = 0.9; // fraction of hash table that can be filled
  private Node<K, V>[] hashTable;
  private int numberOfEntries;
  private Node<K, V> head; // head of the doubly linked list
  private Node<K, V> last; // last of the doubly linked list

  public LinkedHashMap() {
    this(DEFAULT_SIZE);
  }

  @SuppressWarnings("unchecked")
  public LinkedHashMap(int tableSize) {
    int primeSize = getNextPrime(tableSize);

    this.hashTable = (Node<K, V>[]) new Node[primeSize];
    this.numberOfEntries = 0;
    this.head = null;
    this.last = null;
  }

  private static class Node<K, V> implements Entry<K, V> {
    private K key;
    private V value;
    private Node<K, V> next; // Singly linked list to handle collisions
    private Node<K, V> before, after; // Doubly linked list to maintain insertion order

    private Node(K key, V value) {
      this.key = key;
      this.value = value;
    }

    private Node(K key, V value, Node<K, V> next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }

    @Override
    public K getKey() {
      return this.key;
    }

    @Override
    public V getValue() {
      return this.value;
    }
  }

  @Override
  public V put(K key, V value) {
    if (key == null) {
      throw new IllegalArgumentException("Key must be non-null.");
    }

    V oldValue = null; // value to return

    if (isHashTableTooFull()) {
      rehash();
    }

    int index = getHashIndex(key);

    Node<K, V> newEntry = new Node<K, V>(key, value);
    maintainOrderAfterInsert(newEntry);

    // index always in range due to mod of hash fn
    if (hashTable[index] == null) {
      // key not found, so insert new entry
      hashTable[index] = newEntry;
      numberOfEntries++;
    } else {
      // search chain beginning at hashTable[index]
      // for a node that contains key
      Node<K, V> currentNode = hashTable[index];
      Node<K, V> nodeBefore = null;

      while ((currentNode != null) && !key.equals(currentNode.key)) {
        nodeBefore = currentNode;
        currentNode = currentNode.next;
      }

      if (currentNode == null) {
        // key not in chain; add new entry to end of chain
        nodeBefore.next = newEntry;
        numberOfEntries++;
      } else {
        // key found; get old value for return and then replace it
        oldValue = currentNode.value;
        currentNode.value = value;
      }
    }

    return oldValue;
  }

  @Override
  public V get(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key must be non-null.");
    }

    int index = getHashIndex(key);
    Node<K, V> currentNode = hashTable[index];

    while (currentNode != null) {
      if (key.equals(currentNode.key)) {
        return currentNode.value; // return value corresponding to key
      } else {
        currentNode = currentNode.next;
      }
    }

    return null; // returns null if key is not found
  }

  @Override
  public V remove(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key must be non-null.");
    }

    V removedValue = null;

    int index = getHashIndex(key);

    // search chain beginning at hashTable[index] 
    // for a node that contains key
    Node<K, V> nodeBefore = null;
    Node<K, V> currentNode = hashTable[index];

    while ((currentNode != null) && !key.equals(currentNode.key)) {
      nodeBefore = currentNode;
      currentNode = currentNode.next;
    }

    if (currentNode != null) {
      // key found; get value for return and then remove entry
      removedValue = currentNode.value;
      maintainOrderAfterDeletion(currentNode);

      if (nodeBefore == null) {
        // remove first node in the chain
        hashTable[index] = currentNode.next;
      } else {
        nodeBefore.next = currentNode.next;
      }

      numberOfEntries--;
    }
    // else removedValue is null if key not found

    return removedValue;
  }

  @Override
  public boolean isEmpty() {
    return this.numberOfEntries == 0;
  }

  @Override
  public int size() {
    return this.numberOfEntries;
  }

  @Override
  public void clear() {
    this.numberOfEntries = 0;
    for (int i = 0; i < hashTable.length; i++) {
      hashTable[i] = null;
    }
  }

  @Override
  public boolean containsKey(K key) {
    return this.get(key) != null;
  }

  // Support for public keys() method
  private class KeyIterator implements Iterator<K> {
    private Iterator<Entry<K, V>> entries = entries().iterator();

    @Override
    public boolean hasNext() {
      return entries.hasNext();
    }

    @Override
    public K next() {
      return entries.next().getKey();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class KeyIterable implements Iterable<K> {
    @Override
    public Iterator<K> iterator() {
      return new KeyIterator();
    }
  }

  @Override
  public Iterable<K> keys() {
    return new KeyIterable();
  }

  // Support for public values() method
  private class ValueIterator implements Iterator<V> {
    private Iterator<Entry<K, V>> entries = entries().iterator();

    @Override
    public boolean hasNext() {
      return entries.hasNext();
    }

    @Override
    public V next() {
      return entries.next().getValue();
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class ValueIterable implements Iterable<V> {
    @Override
    public Iterator<V> iterator() {
      return new ValueIterator();
    }
  }

  @Override
  public Iterable<V> values() {
    return new ValueIterable();
  }

  // Support for public entries() method
  private class EntryIterator implements Iterator<Entry<K, V>> {
    Node<K, V> nextNode = head;

    @Override
    public boolean hasNext() {
      return this.nextNode != null;
    }

    @Override
    public Entry<K, V> next() {
      Node<K, V> e = nextNode;
      if (e == null) {
        throw new NoSuchElementException();
      }
      nextNode = e.after;
      return e;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class EntryIterable implements Iterable<Entry<K, V>> {
    @Override
    public Iterator<Entry<K, V>> iterator() {
      return new EntryIterator();
    }
  }

  @Override
  public Iterable<Entry<K, V>> entries() {
    return new EntryIterable();
  }

  // -----------------------------------------------------
  // --------------------- Utilities ---------------------
  // -----------------------------------------------------

  /**
   * Help in ensuring insertion order of LinkedHashMap
   * after a new key-value pair is added.
   */
  private void maintainOrderAfterInsert(Node<K, V> newEntry) {
    if (head == null) {
      head = newEntry;
      last = newEntry;
      return;
    }

    if (head.key.equals(newEntry.key)) {
      deleteFirst();
      insertFirst(newEntry);
      return;
    }

    if (last.key.equals(newEntry.key)) {
      deleteLast();
      insertLast(newEntry);
      return;
    }

    Node<K, V> beforeDeleteEntry = deleteSpecificEntry(newEntry);
    if (beforeDeleteEntry == null) {
      insertLast(newEntry);
    } else {
      insertAfter(beforeDeleteEntry, newEntry);
    }
  }

  /**
   * Help in ensuring insertion order of LinkedHashMap
   * after a new key-value pair is deleted.
   */
  private void maintainOrderAfterDeletion(Node<K, V> deleteEntry) {
    if (head.key.equals(deleteEntry.key)) {
      deleteFirst();
      return;
    }

    if (last.key.equals(deleteEntry.key)) {
      deleteLast();
      return;
    }

    deleteSpecificEntry(deleteEntry);
  }

  private void insertAfter(Node<K, V> beforeDeleteEntry, Node<K, V> newEntry) {
    Node<K, V> current = head;
    while (current != beforeDeleteEntry) {
      current = current.after; // move to next node
    }

    newEntry.after = beforeDeleteEntry.after;
    beforeDeleteEntry.after.before = newEntry;
    newEntry.before = beforeDeleteEntry;
    beforeDeleteEntry.after = newEntry;
  }

  /**
   * insert entry at first.
   */
  private void insertFirst(Node<K, V> newEntry) {
    if (head == null) { // no entry found
      head = newEntry;
      last = newEntry;
      return;
    }

    newEntry.after = head;
    head.before = newEntry;
    head = newEntry;
  }

  /**
   * insert entry at last.
   */
  private void insertLast(Node<K, V> newEntry) {
    if (head == null) {
      head = newEntry;
      last = newEntry;
      return;
    }

    last.after = newEntry;
    newEntry.before = last;
    last = newEntry;
  }

  /**
   * delete entry from first.
   */
  private void deleteFirst() {
    if (head == last) { // only one entry found.
      head = last = null;
      return;
    }
    head = head.after;
    head.before = null;
  }

  /**
   * delete entry from last.
   */
  private void deleteLast() {
    if (head == last) {
      head = last = null;
      return;
    }

    last = last.before;
    last.after = null;
  }

  /**
   * delete specific entry and returns before entry.
   */
  private Node<K, V> deleteSpecificEntry(Node<K, V> newEntry) {
    Node<K, V> current = head;
    while (!current.key.equals(newEntry.key)) {
      if (current.after == null) { // entry not found
        return null;
      }
      current = current.after; // move to next node
    }

    Node<K, V> beforeDeleteEntry = current.before;
    current.before.after = current.after;
    current.after.before = current.before; // entry deleted
    return beforeDeleteEntry;
  }

  private int getNextPrime(int integer) {
    // if even, add 1 to make odd
    if (integer % 2 == 0) {
      integer++;
    }

    // test odd integers
    while (!isPrime(integer)) {
      integer = integer + 2;
    }

    return integer;
  }

  private boolean isPrime(int integer) {
    boolean result;
    boolean done = false;

    // 1 and even numbers are not prime
    if ((integer == 1) || (integer % 2 == 0)) {
      result = false;
    } // 2 and 3 are prime
    else if ((integer == 2) || (integer == 3)) {
      result = true;
    } else { // integer is odd and >= 5
      // a prime is odd and not divisible by every odd integer 
      // up to its square root
      result = true; // assume prime
      for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2) {
        if (integer % divisor == 0) {
          result = false; // divisible; not prime
          done = true;
        }
      }
    }

    return result;
  }

  /**
   * @return true if lambda > MAX_LOAD_FACTOR for hash table;
   *         otherwise returns false.
   */
  private boolean isHashTableTooFull() {
    return this.numberOfEntries > MAX_LOAD_FACTOR * hashTable.length;
  }

  /**
   * Task: Increases the size of the hash table 
   * to a prime > twice its old size.
   */
  @SuppressWarnings("unchecked")
  private void rehash() {
    Node<K, V>[] oldTable = this.hashTable;
    int oldSize = oldTable.length;
    int newSize = getNextPrime(oldSize + oldSize);
    
    // increase size of array
    this.hashTable = (Node<K, V>[]) new Node[newSize]; 
    // reset number of entries, since
    // it will be incremented by add during rehash
    this.numberOfEntries = 0; 

    // rehash entries from old array to new, bigger array.
    for (int index = 0; index < oldSize; index++) {
      // rehash chain in old table
      Node<K, V> currentNode = oldTable[index];

      while (currentNode != null) { // skip empty lists
        this.put(currentNode.key, currentNode.value);
        currentNode = currentNode.next;
      }
    }
  }

  private int getHashIndex(K key) {
    int hashIndex = key.hashCode() % hashTable.length;

    if (hashIndex < 0) {
      hashIndex += hashTable.length;
    }

    return hashIndex;
  }
}
