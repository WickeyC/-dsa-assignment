package adt;

import java.util.Comparator;
import java.util.ListIterator;

/** An implementation of an adaptable priority queue using an array-based heap. */
public class HeapAdaptablePriorityQueue<K,V> extends HeapPriorityQueue<K,V> {

//---------------- nested AdaptablePQEntry class ----------------
    /** Extension of the PQEntry to include location information. */
    public static class AdaptablePQEntry<K,V> extends PQEntry<K,V> {
        private int index; // entry’s current index within the heap
        public AdaptablePQEntry(K key, V value, int j) {
         super(key, value); // this sets the key and value
         index = j; // this sets the new field
        }
        public int getIndex( ) { return index; }
        public void setIndex(int j) { index = j; }
    }
//----------- end of nested AdaptablePQEntry class -----------

    /** Creates an empty adaptable priority queue using natural ordering of keys. */
    public HeapAdaptablePriorityQueue( ) { super( ); }
    /** Creates an empty adaptable priority queue using the given comparator. */
    public HeapAdaptablePriorityQueue(Comparator<K> comp) { super(comp);}

    public HeapAdaptablePriorityQueue(K[] keys, V[] values) {
        super();
        int lastIndex = Math.min(keys.length, values.length);
        for (int j=0; j < lastIndex; j++) {
            heap.add(new AdaptablePQEntry<>(keys[j], values[j], j));
        }
        heapify();
    }

    // protected utilites
    /** Validates an entry to ensure it is location-aware. */
    protected AdaptablePQEntry<K,V> validate(Entry<K,V> entry)
    throws IllegalArgumentException {
        if (!(entry instanceof AdaptablePQEntry)) {
            throw new IllegalArgumentException("Invalid entry: wrong data type");
        }
        AdaptablePQEntry<K,V> locator = (AdaptablePQEntry<K,V>) entry; // safe
        int j = locator.getIndex();
        if (j >= heap.size( )) {
            throw new IllegalArgumentException("Invalid entry: out of bounds");
        } if ( (heap.get(j)).getValue() != locator.getValue() ) {
            throw new IllegalArgumentException("Invalid entry: not the same entry");
        }
        return locator;
    }

    public AdaptablePQEntry<K,V> getEntry(int index) {
        if (index < 0 || index > size()){
            throw new IllegalArgumentException("Invalid index: out of bounds");
        } if (isEmpty()) {
            throw new IllegalArgumentException("Queue is empty");
        } 
        AdaptablePQEntry<K,V> targetEntry = null;
        for (Entry<K, V> entry : entries()) {
            if (((AdaptablePQEntry<K,V>)entry).getIndex() == index) {
                targetEntry = (AdaptablePQEntry<K,V>) entry;
                break;
            }
        }

        return (AdaptablePQEntry<K,V>) targetEntry;
        
    }
    
    /** Exchanges the entries at indices i and j of the array list. */
    protected void swap(int i, int j) {
        super.swap(i,j); // perform the swap
        ((AdaptablePQEntry<K,V>) heap.get(i)).setIndex(i); // reset entry's index
        ((AdaptablePQEntry<K,V>) heap.get(j)).setIndex(j); // reset entry's index
    }

    /** Restores the heap property by moving the entry at index j upward/downward.*/
    protected void bubble(int j) {
        if (j > 0 && compare(heap.get(j), heap.get(parent(j))) < 0) {
            upheap(j);
        } else {
            downheap(j); // although it might not need to move
        }
    }

    /** Inserts a key-value pair and returns the entry created. */
    public Entry<K,V> insert(K key, V value) throws IllegalArgumentException {
        checkKey(key); // might throw an exception
        Entry<K,V> newest = new AdaptablePQEntry<>(key, value, heap.size());
        heap.add(newest); // add to the end of the list
        upheap(heap.size() - 1); // upheap newly added entry
        return newest;
    }

    /** Removes the given entry from the priority queue. */
    public void removeEntry(Entry<K,V> entry) throws IllegalArgumentException {
        AdaptablePQEntry<K,V> locator = validate(entry);
        int j = locator.getIndex();
        if (j == heap.size() - 1) { // entry is at last position
            heap.remove(heap.size() - 1); // so just remove it
        } else {
            swap(j, heap.size() - 1); // swap entry to last position
            heap.remove(heap.size() - 1); // then remove it
            bubble(j); // and fix entry displaced by the swap
        }
    }

    /** Replaces the key of an entry. */
    public void replaceKey(Entry<K,V> entry, K key)
    throws IllegalArgumentException {
        AdaptablePQEntry<K,V> locator = validate(entry);
        checkKey(key); // might throw an exception
        locator.setKey(key); // method inherited from PQEntry
        bubble(locator.getIndex()); // with new key, may need to move entry
    }
    /** Replaces the value of an entry. */
    public void replaceValue(Entry<K,V> entry, V value)
        throws IllegalArgumentException {
        AdaptablePQEntry<K,V> locator = validate(entry);
        locator.setValue(value); // method inherited from PQEntry
    }

    protected class HAPQEntryIterator extends PQEntryIterator {
        HAPQEntryIterator() {
           iteratingList =  new ArrayList<Entry<K,V>>();
           int index = 0;
            while(size() > 0) {
                Entry<K, V> min = removeMin();
                ((AdaptablePQEntry<K, V>) min).setIndex(index++);
                iteratingList.add(min);
            }
            for (int i = 0; i < iteratingList.size(); i ++) {
                insert(iteratingList.get(i).getKey(),iteratingList.get(i).getValue());
            }
        }

        @Override
          public void remove() {
            if (indexPos > -1) {
              removeEntry(iteratingList.get(indexPos));
              iteratingList.remove(indexPos);
            } else {
              throw new IllegalStateException();
            }
          }
    }

    protected class HAPQEntryIterable extends PQEntryIterable {
      @Override
      public ListIterator<Entry<K,V>> iterator() {
        return new HAPQEntryIterator();
      }
    }

    @Override
    public Iterable<Entry<K, V>> entries() {
      return new HAPQEntryIterable();
    }
}