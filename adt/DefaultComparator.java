package adt;

import java.util.Comparator;


@SuppressWarnings("unchecked")
public class DefaultComparator<T> implements Comparator<T> {
    public int compare(T a, T b) throws ClassCastException {
        return ((Comparable<T>) a).compareTo(b);
    }
}