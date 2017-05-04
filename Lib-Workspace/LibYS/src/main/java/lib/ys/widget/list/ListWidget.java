package lib.ys.widget.list;

import android.database.DataSetObserver;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ex.NavBar;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.UIUtil;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.widget.list.mix.IMixScrollWidget;
import lib.ys.widget.list.mix.MixOnScrollListener;

/**
 * list组件
 *
 * @author yuansui
 */
public class ListWidget<T> implements OnItemClickListener, OnItemLongClickListener, IMixScrollWidget<T> {

    private ListView mLv;
    private IAdapter<T> mAdapter;

    private View mHeaderView;
    private View mFooterView;
    private RelativeLayout mEmptyView;

    private DataSetObserver mDataSetObserver;

    private OnListWidgetListener<T> mListener;

    public ListWidget(@NonNull OnListWidgetListener<T> listener) {
        if (listener == null) {
            throw new IllegalStateException("OnListWidgetListener must be NonNull");
        }
        mListener = listener;
    }

    @CallSuper
    public void findViews(@NonNull View contentView, @NonNull @IdRes int listId, @Nullable View header, @Nullable View footer, @Nullable View empty) {
        mLv = (ListView) contentView.findViewById(listId);

        LayoutInflater inflater = LayoutInflater.from(contentView.getContext());

        // 在这里添加header和footer, 以便于接着在子类里从header和footer里findview
        if (header != null) {
            mHeaderView = header;

            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            layout.addView(header, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(layout);
            mLv.addHeaderView(layout);
        }

        if (footer != null) {
            mFooterView = footer;

            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            layout.addView(footer, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(layout);
            mLv.addFooterView(layout);
        }

        // 添加empty view
        if (empty != null) {
            mEmptyView = (RelativeLayout) contentView.findViewById(R.id.list_empty_view);
            if (mEmptyView != null) {
                // 有可能布局没有保持要求的格式
                mEmptyView.addView(empty, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
                // 这个时候先不set到list view里
//                mLv.setEmptyView(mEmptyView);
            }
        }
    }

    public void setViews() {
        UIUtil.setOverScrollNever(mLv);

        mLv.setAdapter((ListAdapter) mAdapter);
        mLv.setOnItemClickListener(this);

        if (mListener.enableLongClick()) {
            mLv.setOnItemLongClickListener(this);
        }

        if (!mListener.needDelayAddEmptyView()) {
            addEmptyViewIfNoNull();
        }
    }

    public void addEmptyViewIfNoNull() {
        if (mEmptyView != null) {
            mLv.setEmptyView(mEmptyView);
        }
    }

    public void createAdapter(IAdapter<T> adapter) {
        if (mAdapter != null) {
            return;
        }

        mAdapter = adapter;
        mDataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                mListener.onDataSetChanged();
            }
        };
        mAdapter.registerDataSetObserver(mDataSetObserver);
    }

    public void onDestroy() {
        if (mAdapter != null) {
            if (mDataSetObserver != null) {
                mAdapter.unregisterDataSetObserver(mDataSetObserver);
            }

            mAdapter.removeAll();
        }
    }

    @Override
    public final void onItemClick(AdapterView<?> parent, View v, int position, long duration) {
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int index = getItemRealPosition(position);
        if (index < 0) {
            // 点击的是header区域
            return false;
        }
        if (index >= getCount()) {
            // 点击的是footer区域
            return false;
        }
        mListener.onItemLongClick(view, index);
        return true;
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

    public void setOnScrollListener(OnScrollListener listener) {
        mLv.setOnScrollListener(listener);
    }

    public int getItemRealPosition(int position) {
        return position - mLv.getHeaderViewsCount();
    }

    public int getFirstVisiblePosition() {
        return mLv.getFirstVisiblePosition();
    }

    public View getChildAt(int index) {
        return mLv.getChildAt(index);
    }

    public int getHeaderViewPosition() {
        return mLv.getHeaderViewsCount();
    }

    public void hideFooterView() {
        if (mFooterView != null) {
            ViewUtil.goneView(mFooterView);
        }
    }

    @Override
    public void addFooterView(View v) {
        mLv.addFooterView(v);
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
        mLv.setSelection(position);
    }

    public void smoothScrollToPosition(int position) {
        mLv.smoothScrollToPosition(position);
    }

    public ListView getLv() {
        return mLv;
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

    public void setDividerHeight(int height) {
        mLv.setDividerHeight(height);
    }

    /**
     * 根据高度自动变换titleBar的背景色透明度
     *
     * @param height
     * @param navBar
     */
    public void setNavBarAutoAlphaByScroll(final int height, final NavBar navBar) {
        int th = navBar.getHeight();
        if (th == 0 && navBar.getVisibility() != View.GONE && navBar.getViewTreeObserver().isAlive()) {
            // 调用的时机不对. 获取不到titleBar的高度
            navBar.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    int th = navBar.getHeight();
                    if (th == 0) {
                        return true;
                    }
                    final int h = height - th;

                    // 注意要用listener调用, 因为对于SRList来说, scroll的监听使用的方式不一样
                    mListener.setOnScrollListener(new MixOnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            computeTitleBarAlpha(view, firstVisibleItem, navBar, h);
                        }
                    });

                    navBar.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            final int h = height - th;
            mListener.setOnScrollListener(new MixOnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    computeTitleBarAlpha(view, firstVisibleItem, navBar, h);
                }
            });
        }
    }

    private void computeTitleBarAlpha(AbsListView view, int firstVisibleItem, NavBar navBar, int h) {
        if (firstVisibleItem == 0) {
            float top = -view.getChildAt(0).getTop();
            float rate = top / h;
            if (rate > 1) {
                rate = 1;
            }
            navBar.setBackgroundAlpha((int) (rate * ConstantsEx.KAlphaMax));
        } else if (firstVisibleItem > 0) {
            navBar.setBackgroundAlpha(ConstantsEx.KAlphaMax);
        } else {
            navBar.setBackgroundAlpha(0);
        }
    }
}
