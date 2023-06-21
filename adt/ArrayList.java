package adt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayList<T> implements ListInterface<T>, Serializable {

  private T[] array;
  private int numberOfEntries;
  private static final int DEFAULT_CAPACITY = 5;

  public ArrayList() {
    this(DEFAULT_CAPACITY);
  }

  @SuppressWarnings("unchecked")
  public ArrayList(int initialCapacity) {
    numberOfEntries = 0;
    array = (T[]) new Object[initialCapacity];
  }

  @Override
  public boolean add(T newEntry) {
    // check if not enough capacity
    if (isArrayFull()) {
      resize(2 * array.length); // so double the current capacit
    }

    array[numberOfEntries] = newEntry;
    numberOfEntries++;
    return true;
  }

  @Override
  public boolean add(int newPosition, T newEntry) {
    // check if not enough capacity
    if (isArrayFull()) {
      resize(2 * array.length); // so double the current capacit
    }

    boolean isSuccessful = true;

    if ((newPosition >= 0) && (newPosition <= numberOfEntries)) {
      makeRoom(newPosition);
      array[newPosition] = newEntry;
      numberOfEntries++;
    } else {
      isSuccessful = false;
    }

    return isSuccessful;
  }

  @Override
  public T remove(int givenPosition) {
    T result = null;

    if ((givenPosition >= 0) && (givenPosition < numberOfEntries)) {
      result = array[givenPosition];

      if (givenPosition < numberOfEntries - 1) {
        removeGap(givenPosition);
      }

      this.numberOfEntries--;
    }

    return result;
  }

  @Override
  public void clear() {
    this.numberOfEntries = 0;
  }

  @Override
  public boolean replace(int givenPosition, T newEntry) {
    boolean isSuccessful = true;

    if ((givenPosition >= 0) && (givenPosition < numberOfEntries)) {
      array[givenPosition] = newEntry;
    } else {
      isSuccessful = false;
    }

    return isSuccessful;
  }

  @Override
  public T get(int givenPosition) {
    T result = null;

    if ((givenPosition >= 0) && (givenPosition < numberOfEntries)) {
      result = array[givenPosition];
    }

    return result;
  }

  @Override
  public boolean contains(T anEntry) {
    boolean found = false;
    for (int index = 0; !found && (index < numberOfEntries); index++) {
      if (anEntry.equals(array[index])) {
        found = true;
      }
    }
    return found;
  }

  @Override
  public int size() {
    return numberOfEntries;
  }

  @Override
  public boolean isEmpty() {
    return numberOfEntries == 0;
  }

  @Override
  public boolean isFull() {
    return false;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T[] toArray(T[] a) {
    if (a.length < numberOfEntries) {
      return (T[]) Arrays.copyOf(array, numberOfEntries, a.getClass());
    }
    for (int i = 0; i < numberOfEntries; i++) {
      a[i] = array[i];
    }
    return a;
  }

  @Override
  public String toString() {
    String outputStr = "";
    for (int index = 0; index < numberOfEntries; ++index) {
      outputStr += array[index] + "\n";
    }

    return outputStr;
  }

  /**
   * Task: Makes room for a new entry at newPosition. Precondition: 0 <=
   * newPosition <= numberOfEntries; numberOfEntries is array's numberOfEntries
   * before addition.
   */
  private void makeRoom(int newPosition) {
    int newIndex = newPosition;
    int lastIndex = numberOfEntries - 1;

    // move each entry to next higher index, starting at end of
    // array and continuing until the entry at newIndex is moved
    for (int index = lastIndex; index >= newIndex; index--) {
      array[index + 1] = array[index];
    }
  }

  /**
   * Task: Shifts entries that are beyond the entry to be removed to the next
   * lower position. Precondition: array is not empty; 0 <= givenPosition <
   * numberOfEntries - 1; numberOfEntries is array's numberOfEntries before
   * removal.
   */
  private void removeGap(int givenPosition) {
    // move each entry to next lower position starting at entry after the
    // one removed and continuing until end of array
    int removedIndex = givenPosition;
    int lastIndex = numberOfEntries - 1;

    for (int index = removedIndex; index < lastIndex; index++) {
      array[index] = array[index + 1];
    }
  }

  private boolean isArrayFull() {
    return numberOfEntries == array.length;
  }

  @SuppressWarnings("unchecked")
  private void resize(int newSize) {
    T[] temp = (T[]) new Object[newSize]; // safe cast; compiler may give warning for (int k=0; k < size; k++)
    for (int k = 0; k < numberOfEntries; k++) {
      temp[k] = array[k];
    }
    array = temp;
  }

  @Override
  public Iterator<T> iterator() {
    return new ArrayListIterator();
  }

  private class ArrayListIterator implements Iterator<T> {
    /** index of current object */
    private int index = 0;

    @Override
    public boolean hasNext() {
      return index < numberOfEntries;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new IndexOutOfBoundsException();
      }
      return array[index++];
    }
  }
}