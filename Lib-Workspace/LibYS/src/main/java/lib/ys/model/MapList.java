package lib.ys.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 带有map功能的list
 * PS: 不要使用 deprecated 申明的方法, 会无法绑定map
 *
 * @author yuansui
 */
public class MapList<K, E> extends ArrayList<E> {

    private Map<K, E> mMap;

    public MapList() {
        super();

        mMap = new HashMap<>();
    }

    /**
     * @deprecated use {@link #has(Object)} instead
     */
    @Override
    public boolean add(E e) {
        return super.add(e);
    }

    public void add(K key, int index, E element) {
        mMap.put(key, element);
        super.add(index, element);
    }

    public boolean add(K key, E e) {
        mMap.put(key, e);
        return super.add(e);
    }

    /**
     * @deprecated use {@link #has(Object)} instead
     */
    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    /**
     * @deprecated use {@link #has(Object)} instead
     */
    @Override
    public E remove(int index) {
        return super.remove(index);
    }

    @Override
    public void clear() {
        mMap.clear();
        super.clear();
    }

    /**
     * @deprecated use {@link #has(Object)} instead
     */
    @Override
    public boolean contains(Object o) {
        return super.contains(o);
    }

    public boolean has(K key) {
        return mMap.get(key) != null;
    }

    public boolean removeByKey(K key) {
        E e = mMap.remove(key);
        return super.remove(e);
    }

    public E getByKey(K key) {
        return mMap.get(key);
    }
}
