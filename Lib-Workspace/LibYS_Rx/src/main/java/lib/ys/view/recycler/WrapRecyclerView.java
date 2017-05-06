package lib.ys.view.recycler;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 网上代码, 未整理
 */
public class WrapRecyclerView extends RecyclerView {

    private WrapAdapter mWrapAdapter;
    private boolean shouldAdjustSpanSize;

    // 临时头部View集合,用于存储没有设置Adapter之前添加的头部
    private ArrayList<View> mTmpHeaderView = new ArrayList<>();
    // 临时尾部View集合,用于存储没有设置Adapter之前添加的尾部
    private ArrayList<View> mTmpFooterView = new ArrayList<>();

    public WrapRecyclerView(Context context) {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof WrapAdapter) {
            mWrapAdapter = (WrapAdapter) adapter;
            super.setAdapter(adapter);
        } else {
            mWrapAdapter = new WrapAdapter(adapter);
            for (View view : mTmpHeaderView) {
                mWrapAdapter.addHeaderView(view);
            }
            if (mTmpHeaderView.size() > 0) {
                mTmpHeaderView.clear();
            }

            for (View view : mTmpFooterView) {
                mWrapAdapter.addFooterView(view);
            }
            if (mTmpFooterView.size() > 0) {
                mTmpFooterView.clear();
            }

            super.setAdapter(mWrapAdapter);
        }

        if (shouldAdjustSpanSize) {
            mWrapAdapter.adjustSpanSize(this);
        }

        getWrappedAdapter().registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    /**
     * Retrieves the previously set wrap adapter or null if no adapter is set.
     *
     * @return The previously set adapter
     */
    @Override
    public WrapAdapter getAdapter() {
        return mWrapAdapter;
    }

    /**
     * Gets the real adapter
     *
     * @return T:
     * @version 1.0
     */
    public Adapter getWrappedAdapter() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getWrappedAdapter();
    }

    /**
     * Adds a header view
     *
     * @param v
     * @version 1.0
     */
    public void addHeaderView(View v) {
        if (null == v) {
            throw new IllegalArgumentException("the view to addToMgr must not be null!");
        } else if (mWrapAdapter == null) {
            mTmpHeaderView.add(v);
        } else {
            mWrapAdapter.addHeaderView(v);
        }
    }

    /**
     * Adds a footer view
     *
     * @param view
     * @version 1.0
     */
    public void addFooterView(View view) {
        if (null == view) {
            throw new IllegalArgumentException("the view to addToMgr must not be null!");
        } else if (mWrapAdapter == null) {
            mTmpFooterView.add(view);
        } else {
            mWrapAdapter.addFooterView(view);
        }
    }

    /**
     * Adds a footer view
     *
     * @param view
     * @param reverse
     */
    public void addFooterView(View view, boolean reverse) {
        if (null == view) {
            throw new IllegalArgumentException("the view to addToMgr must not be null!");
        } else if (mWrapAdapter == null) {
            mTmpFooterView.add(view);
        } else {
            mWrapAdapter.addFooterView(view, reverse);
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager || layout instanceof StaggeredGridLayoutManager) {
            this.shouldAdjustSpanSize = true;
        }
    }

    /**
     * gets the headers view
     *
     * @return List:
     * @version 1.0
     */
    public List<View> getHeadersView() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getHeadersView();
    }

    /**
     * gets the footers view
     *
     * @return List:
     * @version 1.0
     */
    public List<View> getFootersView() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getFootersView();
    }

    /**
     * Setting the visibility of the header views
     *
     * @param shouldShow
     * @version 1.0
     */
    public void setFooterVisibility(boolean shouldShow) {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        mWrapAdapter.setFooterVisibility(shouldShow);
    }

    /**
     * Setting the visibility of the footer views
     *
     * @param shouldShow
     * @version 1.0
     */
    public void setHeaderVisibility(boolean shouldShow) {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        mWrapAdapter.setHeaderVisibility(shouldShow);
    }

    /**
     * get the count of headers
     *
     * @return number of headers
     * @version 1.0
     */
    public int getHeadersCount() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getHeadersCount();
    }

    /**
     * get the count of footers
     *
     * @return the number of footers
     * @version 1.0
     */
    public int getFootersCount() {
        if (mWrapAdapter == null) {
            throw new IllegalStateException("You must set a adapter before!");
        }
        return mWrapAdapter.getFootersCount();
    }

    private final AdapterDataObserver mDataObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            if (mWrapAdapter != null) {
                mWrapAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    public int getFirstVisiblePosition() {
        LayoutManager m = getLayoutManager();
        if (m instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) m).findFirstVisibleItemPosition();
        } else if (m instanceof StaggeredGridLayoutManager) {
            //因为StaggeredGridLayoutManager的特殊性可能导致最后显示的item存在多个，所以这里取到的是一个数组
            int[] lastPositions = new int[((StaggeredGridLayoutManager) m).getSpanCount()];
            ((StaggeredGridLayoutManager) m).findFirstVisibleItemPositions(lastPositions);
            int min = lastPositions[0];
            for (int value : lastPositions) {
                if (value < min) {
                    min = value;
                }
            }
            return min;
        } else if (m instanceof GridLayoutManager) {
            return ((GridLayoutManager) m).findFirstVisibleItemPosition();
        }
        return 0;
    }

    @Nullable
    public View findViewInHeaderById(@IdRes int id) {
        View ret = null;
        for (View v : mTmpHeaderView) {
            ret = v.findViewById(id);
            if (ret != null) {
                break;
            }
        }
        return ret;
    }

    @Nullable
    public View findViewInFooterById(@IdRes int id) {
        View ret = null;
        for (View v : mTmpFooterView) {
            ret = v.findViewById(id);
            if (ret != null) {
                break;
            }
        }
        return ret;
    }


}