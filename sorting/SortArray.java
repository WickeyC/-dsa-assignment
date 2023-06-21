package sorting;

public class SortArray {
  private static <T> void swap(T[] a, int i, int j) {
    T temp = a[i];
    a[i] = a[j];
    a[j] = temp;
  }

  /**
   * ********************* Quick Sort ******************************
   */
  public static <T extends Comparable<T>> void quickSort(T[] a, int n) {
    quickSort(a, 0, n - 1);
  }

  // recursive private overloaded method quickSort() 
  private static <T extends Comparable<T>> void quickSort(T[] a, int first, int last) {
    if (first < last) {
      int pivotIndex = partition(a, first, last);
      quickSort(a, first, pivotIndex - 1);
      quickSort(a, pivotIndex + 1, last);
    }
  }

  // private method partition() - to partition the array based on the pivot value
  private static <T extends Comparable<T>> int partition(T[] a, int first, int last) {
    int pivotIndex = last;
    T pivot = a[last];
    int indexFromLeft = first;
    int indexFromRight = last - 1;
    boolean done = false;

    while (!done) {
      while (indexFromLeft < last && a[indexFromLeft].compareTo(pivot) < 0) {
        indexFromLeft++;
      }

      while (indexFromRight > first && a[indexFromRight].compareTo(pivot) > 0) {
        indexFromRight--;
      }

      if (indexFromLeft < indexFromRight) {
        swap(a, indexFromLeft, indexFromRight);
        indexFromLeft++;
        indexFromRight--;
      } else {
        done = true;
      }
    }
    swap(a, pivotIndex, indexFromLeft);
    pivotIndex = indexFromLeft;
    return pivotIndex;
  }
}