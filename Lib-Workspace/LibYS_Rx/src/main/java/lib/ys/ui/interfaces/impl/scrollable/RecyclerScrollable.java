package lib.ys.ui.interfaces.impl.scrollable;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.View;

import java.util.List;

import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.adapter.recycler.OnRecyclerItemClickListener;
import lib.ys.ui.interfaces.listener.scrollable.OnRecyclerScrollableListener;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.view.recycler.WrapRecyclerView;

/**
 * list组件
 *
 * @author yuansui
 */
public class RecyclerScrollable<T, A extends IAdapter<T>> extends BaseScrollable<T> {

    private WrapRecyclerView mRv;

    private Class<A> mAdapterClass;
    private A mAdapter;

    private AdapterDataObserver mDataObserver;

    private OnRecyclerScrollableListener<T, A> mListener;

    private OnRecyclerItemClickListener mClickLsn;


    public RecyclerScrollable(@NonNull OnRecyclerScrollableListener<T, A> l) {
        super(l);

        mListener = l;

        // 这里注意要用listener的class才能获取到正确的adapter class
        mAdapterClass = GenericUtil.getClassType(l.getClass(), IAdapter.class);

        mClickLsn = new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
//                int index = getItemRealPosition(position);
//                if (index < 0) {
//                    // 点击的是header区域
//                    mListener.onHeaderClick(v);
//                    return;
//                }
//                if (index >= getCount()) {
//                    // 点击的是footer区域
//                    mListener.onFooterClick(v);
//                    return;
//                }
//                mListener.onItemClick(v, index);
                mListener.onItemClick(v, position);
            }

            @Override
            public void onItemLongClick(View v, int position) {
//                int index = getItemRealPosition(position);
//                if (index < 0) {
//                    // 点击的是header区域
//                    return;
//                }
//                if (index >= getCount()) {
//                    // 点击的是footer区域
//                    return;
//                }
//                mListener.onItemLongClick(v, index);
            }
        };
    }

    @Override
    public void findViews(@NonNull View contentView, @IdRes int scrollableId, @Nullable View header, @Nullable View footer, @Nullable View empty) {
        super.findViews(contentView, scrollableId, header, footer, empty);
        mRv = (WrapRecyclerView) contentView.findViewById(scrollableId);
    }

    /**
     * do nothing
     *
     * @deprecated use {@link #setViews(LayoutManager, ItemDecoration, ItemAnimator)} instead
     */
    @Override
    public void setViews() {
    }

    @Override
    public <VIEW extends View> VIEW getScrollableView() {
        return (VIEW) mRv;
    }

    public void setViews(LayoutManager manager, ItemDecoration decoration, ItemAnimator animator) {
        createAdapter();

        mRv.setLayoutManager(manager);

        MultiRecyclerAdapterEx adapter = (MultiRecyclerAdapterEx) mAdapter;
        mRv.setAdapter(adapter);
        adapter.setOnItemClickListener(mClickLsn);
        adapter.setEnableLongClick(mListener.enableLongClick());

        // 有bug, 暂时不使用
//        mRv.addOnItemTouchListener(new RecyclerItemClickListener(this, mListener.enableLongClick()));

        if (animator != null) {
            mRv.setItemAnimator(animator);
        }

        if (decoration != null) {
            mRv.addItemDecoration(decoration);
        }

        if (!mListener.needDelayAddEmptyView()) {
            addEmptyViewIfNonNull();
        }
    }

    public void createAdapter() {
        if (mAdapter != null) {
            return;
        }

        mAdapter = ReflectionUtil.newInst(mAdapterClass);
        mDataObserver = new AdapterDataObserver() {

            @Override
            public void onChanged() {
                mListener.onDataSetChanged();
            }
        };

//        mAdapter.registerDataSetObserver(mDataObserver);
    }

    public void onDestroy() {
        if (mAdapter != null) {
            if (mDataObserver != null) {
//            mAdapter.unregisterDataSetObserver(mDataObserver);
            }
            mAdapter.removeAll();
        }
    }

    public void setData(List<T> data) {
        getAdapter().setData(data);
    }

    public void addItem(T item) {
        getAdapter().add(item);
    }

    public void addItem(int position, T item) {
        getAdapter().add(position, item);
    }

    public void addAll(List<T> data) {
        getAdapter().addAll(data);
    }

    public void addAll(int position, List<T> item) {
        getAdapter().addAll(position, item);
    }

    public void invalidate() {
        getAdapter().notifyDataSetChanged();
    }

    public void remove(int position) {
        getAdapter().remove(position);
    }

    public void remove(T item) {
        getAdapter().remove(item);
    }

    public void removeAll() {
        getAdapter().removeAll();
    }

    public List<T> getData() {
        return getAdapter().getData();
    }

    public int getCount() {
        return getAdapter().getCount();
    }

    public int getLastItemPosition() {
        return getAdapter().getLastItemPosition();
    }

    public T getItem(int position) {
        return getAdapter().getItem(position);
    }

    public boolean isEmpty() {
        return getAdapter().isEmpty();
    }

    public void setOnAdapterClickListener(OnAdapterClickListener listener) {
        getAdapter().setOnAdapterClickListener(listener);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        mRv.addOnScrollListener(listener);
    }

    public int getItemRealPosition(int position) {
        return position - mRv.getHeadersCount();
    }

    public int getFirstVisiblePosition() {
        return mRv.getFirstVisiblePosition();
    }

    public int getHeaderViewPosition() {
        return mRv.getHeadersCount();
    }

    public void setSelection(int position) {
        mRv.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        mRv.smoothScrollToPosition(position);
    }

    public A getAdapter() {
        if (mAdapter == null) {
            createAdapter();
        }
        return mAdapter;
    }

    public boolean isAdapterNull() {
        return mAdapter == null;
    }
}
