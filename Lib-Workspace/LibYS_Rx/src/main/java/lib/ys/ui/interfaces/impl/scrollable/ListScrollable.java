package lib.ys.ui.interfaces.impl.scrollable;

import android.database.DataSetObserver;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import lib.ys.ConstantsEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.adapter.interfaces.IAdapter;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.ui.interfaces.listener.scrollable.OnListScrollableListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.GenericUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.util.UIUtil;

/**
 * list组件
 *
 * @author yuansui
 */
public class ListScrollable<T, A extends IAdapter<T>> extends BaseScrollable<T>
        implements OnItemClickListener, OnItemLongClickListener {

    private ListView mLv;

    private Class<A> mAdapterClass;
    private A mAdapter;

    private DataSetObserver mDataSetObserver;

    protected OnListScrollableListener<T, A> mListener;

    public ListScrollable(@NonNull OnListScrollableListener<T, A> l) {
        super(l);

        mListener = l;

        // 这里注意要用listener的class才能获取到正确的adapter class
        mAdapterClass = GenericUtil.getClassType(l.getClass(), IAdapter.class);
    }

    @Override
    public <VIEW extends View> VIEW getScrollableView() {
        return (VIEW) mLv;
    }

    @Override
    public void findViews(@NonNull View contentView, @IdRes int scrollableId, @Nullable View header, @Nullable View footer, @Nullable View empty) {
        super.findViews(contentView, scrollableId, header, footer, empty);
        mLv = (ListView) contentView.findViewById(scrollableId);
    }

    @Override
    public void setViews() {
        createAdapter();

        UIUtil.setOverScrollNever(mLv);

        mLv.setAdapter((ListAdapter) mAdapter);
        mLv.setOnItemClickListener(this);

        if (mListener.enableLongClick()) {
            mLv.setOnItemLongClickListener(this);
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
            mAdapter = null;
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

    public int getHeaderViewPosition() {
        return mLv.getHeaderViewsCount();
    }

    public void setSelection(int position) {
        mLv.setSelection(position);
    }

    public void smoothScrollToPosition(int position) {
        mLv.smoothScrollToPosition(position);
    }

    public A getAdapter() {
        if (mAdapter == null) {
            createAdapter();
        }
        return mAdapter;
    }

    public void setDividerHeight(int height) {
        mLv.setDividerHeight(height);
    }

    /**
     * 根据高度自动变换titleBar的背景色透明度
     *
     * @param height 变换透明的总高度
     * @param bar    需要变换的navBar
     */
    public void changeAlphaByScroll(final int height, final NavBar bar) {
        if (bar.getHeight() == 0 && bar.getVisibility() != View.GONE && bar.getViewTreeObserver().isAlive()) {
            // 调用的时机不对. 获取不到titleBar的高度
            bar.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    int barH = bar.getHeight();
                    if (barH == 0) {
                        return true;
                    }
                    final int realH = height - barH;

                    // 注意要用listener调用, 因为对于SRList来说, scroll的监听使用的方式不一样
                    mListener.setOnScrollListener(new OnScrollMixListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            setNavBarAlpha(view, firstVisibleItem, bar, realH);
                        }
                    });

                    bar.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        } else {
            final int realH = height - bar.getHeight();
            mListener.setOnScrollListener(new OnScrollMixListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    setNavBarAlpha(view, firstVisibleItem, bar, realH);
                }
            });
        }
    }

    private void setNavBarAlpha(AbsListView view, int firstVisibleItem, NavBar navBar, int height) {
        if (firstVisibleItem == 0) {
            float top = -view.getChildAt(0).getTop();
            float rate = top / height;
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
