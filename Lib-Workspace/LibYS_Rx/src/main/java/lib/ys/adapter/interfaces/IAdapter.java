package lib.ys.adapter.interfaces;

import java.util.List;

import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;


/**
 * @author yuansui
 */
public interface IAdapter<T> {

    /**
     * 注册数据变化监听
     *
     * @param observer 使用Object是为了兼容RecycleAdapter
     */
    void registerDataSetObserver(Object observer);

    /**
     * 取消数据监听
     *
     * @param observer 使用Object是为了兼容RecycleAdapter
     */
    void unregisterDataSetObserver(Object observer);

    void setOnAdapterClickListener(OnAdapterClickListener listener);

    boolean isEmpty();

    T getItem(int position);

    int getLastItemPosition();

    int getCount();

    List<T> getData();

    void removeAll();

    void remove(int position);

    void remove(T item);

    void notifyDataSetChanged();

    void addAll(int position, List<T> item);

    void addAll(List<T> data);

    void add(T item);

    void add(int position, T item);

    void setData(List<T> data);
}
