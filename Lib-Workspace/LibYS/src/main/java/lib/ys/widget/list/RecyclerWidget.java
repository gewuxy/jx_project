package lib.ys.widget.list;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.adapter.interfaces.OnRecyclerItemClickListener;
import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.recycler.WrapRecyclerView;
import lib.ys.widget.list.mix.IMixScrollWidget;

/**
 * list组件
 *
 * @author yuansui
 */
public class RecyclerWidget<T> implements IMixScrollWidget<T> {

    private WrapRecyclerView mRv;
    private IAdapter<T> mAdapter;

    private View mHeaderView;
    private View mFooterView;
    private RelativeLayout mEmptyView;

    private AdapterDataObserver mDataObserver;

    private OnRecyclerWidgetListener<T> mListener;

    private OnRecyclerItemClickListener mClickLsn;


    public RecyclerWidget(@NonNull OnRecyclerWidgetListener<T> listener) {
        if (listener == null) {
            throw new IllegalStateException("OnRecyclerWidgetListener must be NonNull");
        }
        mListener = listener;

        mClickLsn = new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {
                int index = getItemRealPosition(position);
                if (index < 0) {
                    // 点击的是header区域
                    mListener.onHeaderClick(v);
                    return;
                }
                if (index >= getCount()) {
                    // 点击的是footer区域
                    mListener.onFooterClick(v);
                    return;
                }
                mListener.onItemClick(v, index);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                int index = getItemRealPosition(position);
                if (index < 0) {
                    // 点击的是header区域
                    return;
                }
                if (index >= getCount()) {
                    // 点击的是footer区域
                    return;
                }
                mListener.onItemLongClick(v, index);
            }
        };
    }

    public void findViews(View contentView, @IdRes int viewId, View headerView, View footerView, View emptyView) {
        mRv = (WrapRecyclerView) contentView.findViewById(viewId);

        LayoutInflater inflater = LayoutInflater.from(contentView.getContext());

        // 在这里添加header和footer, 以便于接着在子类里从header和footer里findview
        if (headerView != null) {
            mHeaderView = headerView;

            RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            rootView.addView(headerView, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(rootView);
            mRv.addHeaderView(rootView);
        }

        if (footerView != null) {
            mFooterView = footerView;

            RelativeLayout rootView = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            rootView.addView(footerView, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(rootView);
            mRv.addFooterView(rootView);
        }

        // 添加empty view
        if (emptyView != null) {
            mEmptyView = (RelativeLayout) contentView.findViewById(R.id.list_empty_view);
            if (mEmptyView != null) {
                // 有可能布局没有保持要求的格式
                mEmptyView.addView(emptyView, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
                // 这个时候先不set到list view里
//                mRv.setEmptyView(mEmptyView);
            }
        }
    }

    public void setViews(LayoutManager manager, ItemDecoration decoration, ItemAnimator animator) {
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
            addEmptyViewIfNoNull();
        }
    }

    public void addEmptyViewIfNoNull() {
        if (mEmptyView != null) {
//            mRv.setEmptyView(mEmptyView);
        }
    }

    public void createAdapter(IAdapter adapter) {
        if (mAdapter != null) {
            return;
        }

        mAdapter = adapter;
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

    public View getChildAt(int index) {
        return mRv.getChildAt(index);
    }

    public int getHeaderViewPosition() {
        return mRv.getHeadersCount();
    }

    public void hideFooterView() {
        if (mFooterView != null) {
            ViewUtil.goneView(mFooterView);
        }
    }

    @Override
    public void addFooterView(View v) {
        if (mRv != null) {
            mRv.addFooterView(v);
        }
    }

    public void showFooterView() {
        if (mFooterView != null) {
            ViewUtil.showView(mFooterView);
        }
    }

    public void showHeaderView() {
        if (mHeaderView != null) {
            ViewUtil.showView(mHeaderView);
        }
    }

    public void hideHeaderView() {
        if (mHeaderView != null) {
            ViewUtil.goneView(mHeaderView);
        }
    }

    public void setSelection(int position) {
        mRv.scrollToPosition(position);
    }

    public void smoothScrollToPosition(int position) {
        mRv.smoothScrollToPosition(position);
    }

    public WrapRecyclerView getRv() {
        return mRv;
    }

    public IAdapter<T> getAdapter() {
        if (mAdapter == null) {
            createAdapter(mListener.createAdapter());
        }
        return mAdapter;
    }

    public boolean isAdapterNull() {
        return mAdapter == null;
    }
}
