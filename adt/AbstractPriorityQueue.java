package adt;

import java.util.Comparator;
import java.util.ListIterator;

/*  An abstract base class to assist implementations of the PriorityQueue interface.*/
public abstract class AbstractPriorityQueue<K,V> implements PriorityQueue<K,V> {

    //---------------- nested PQEntry class ----------------
    public static class PQEntry<K,V> implements Entry<K,V> {
    private K key; // key
    private V value; // value
    public PQEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }
    // methods of the Entry interface
    public K getKey( ) { return key; }
    public V getValue( ) { return value; }
    // utilities not exposed as part of the Entry interface
    protected void setKey(K key) { this.key = key; }
    protected void setValue(V value) { this.value = value; }
    } //----------- end of nested PQEntry class -----------

    // instance variable for an AbstractPriorityQueue
    /* The comparator defining the ordering of keys in the priority queue. */
    private Comparator<K> comp;
    
    /* Creates an empty priority queue using the given comparator to order keys. */
    protected AbstractPriorityQueue(Comparator<K> c) { comp = c; }
    /* Creates an empty priority queue based on the natural ordering of its keys. */
    protected AbstractPriorityQueue( ) { this(new DefaultComparator<K>( )); }

    /* Method for comparing two entries according to key */
    protected int compare(Entry<K,V> a, Entry<K,V> b) {
        return comp.compare(a.getKey( ), b.getKey( ));
    }

    /* Determines whether a key is valid. */
    protected boolean checkKey(K key) throws IllegalArgumentException {
        try {
            return (comp.compare(key,key) == 0); // see if key can be compared to itself
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incompatible key");
        }
    }

    /* Tests whether the priority queue is empty. */
    public boolean isEmpty( ) { return size( ) == 0; }

    // Support for public keys() method
    private class PQKeyIterator implements ListIterator<K> {
        private ListIterator<Entry<K, V>> entries = (ListIterator<Entry<K, V>>) entries().iterator(); // reuse entries()
  
        @Override
        public boolean hasNext() {
          return entries.hasNext();
        }
  
        @Override
        public K next() {
          return entries.next().getKey();
        }
  
        @Override
        public void remove() {
          entries.remove();
        }
  
        @Override
        public boolean hasPrevious() {
          return entries.hasPrevious();
        }
  
        @Override
        public K previous() {
          return entries.previous().getKey();
        }
  
        @Override
        public int nextIndex() {throw new UnsupportedOperationException();}
        @Override
        public int previousIndex() {throw new UnsupportedOperationException();}
        @Override
        public void set(K e) {throw new UnsupportedOperationException();}
        @Override
        public void add(K e) {throw new UnsupportedOperationException();}
      }
  
      private class PQKeyIterable implements Iterable<K> {
        @Override
        public ListIterator<K> iterator() {
          return new PQKeyIterator();
        }
      }
  
      public Iterable<K> keys() {
        return new PQKeyIterable();
      }
  
      // Support for public values() method
      private class PQValueIterator implements ListIterator<V> {
        private ListIterator<Entry<K, V>> entries =  (ListIterator<Entry<K, V>>) entries().iterator(); // reuse entries()
  
        @Override
        public boolean hasNext() {
          return entries.hasNext();
        }
  
        @Override
        public V next() {
          return entries.next().getValue();
        }
  
        @Override
        public boolean hasPrevious() {
          return entries.hasPrevious();
        }
  
        @Override
        public V previous() {
          return entries.previous().getValue();
        }
    
        @Override
        public void remove() {
          entries.remove();
        }
  
        @Override
        public int nextIndex() {throw new UnsupportedOperationException();}
        @Override
        public int previousIndex() {throw new UnsupportedOperationException();}
        @Override
        public void set(V e) {throw new UnsupportedOperationException();}
        @Override
        public void add(V e) {throw new UnsupportedOperationException();}
      }
  
      private class PQValueIterable implements Iterable<V> {
        @Override
        public ListIterator<V> iterator() {
          return new PQValueIterator();
        }
      }
  
      public Iterable<V> values() {
        return new PQValueIterable();
      }
  
      // Support for public entries() method
      protected class PQEntryIterator implements ListIterator<Entry<K,V>> {
          protected int indexPos = -1;
          protected ArrayList<Entry<K,V>> iteratingList;


          PQEntryIterator() {
             iteratingList =  new ArrayList<Entry<K,V>>();
              while(size() > 0) {
                  iteratingList.add(removeMin());
              }
              for (int i = 0; i < iteratingList.size(); i ++) {
                  insert(iteratingList.get(i).getKey(),iteratingList.get(i).getValue());
              }
          }

          @Override
          public boolean hasNext() {
              return iteratingList.size() > (indexPos + 1);
          }
          @Override
          public Entry<K, V> next() {      
              return iteratingList.get(++indexPos);
          }
  
          @Override
          public boolean hasPrevious() {
              return indexPos > 0;
          }
  
          @Override
          public Entry<K, V> previous() {
              return iteratingList.get(--indexPos);
          }
  
          @Override
          public void remove() { throw new UnsupportedOperationException();}
          @Override
          public int nextIndex() {throw new UnsupportedOperationException();}
          @Override
          public int previousIndex() {throw new UnsupportedOperationException();}
          @Override
          public void set(Entry<K, V> e) {throw new UnsupportedOperationException();}
          @Override
          public void add(Entry<K, V> e) {throw new UnsupportedOperationException();}
      }
  
      protected class PQEntryIterable implements Iterable<Entry<K,V>> {
        @Override
        public ListIterator<Entry<K,V>> iterator() {
          return new PQEntryIterator();
        }
      }
  
      public Iterable<Entry<K, V>> entries() {
        return new PQEntryIterable();
      }
 }
