// Kong Ji Shou
package adt;

public interface Entry<K, V> {
    K getKey();
    V getValue();
    V setValue(V v);
}
