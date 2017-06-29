package lib.ys.view.swipeRefresh.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.ui.interfaces.listener.OnScrollMixListener;
import lib.ys.util.DeviceUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.view.swipeRefresh.footer.BaseFooter;
import lib.ys.view.swipeRefresh.footer.DefaultFooter;
import lib.ys.view.swipeRefresh.interfaces.IExtend.IExtendStatus;

/**
 * @author yuansui
 */
abstract public class BaseSRLoadMoreLayout<T extends View> extends BaseSRLayout<T> {

    private BaseFooter mLoadMoreFooterView;

    private OnScrollMixListener mScrollListener;

    private boolean mIsLoadingMore;
    private boolean mAutoLoadMoreEnabled = true;
    private boolean mCurrState = true;

    public BaseSRLoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) {
            return;
        }

        createFooterView(context);
        addLoadMoreFooterView(mLoadMoreFooterView);

        final View v = getContentView();
        if (v instanceof AbsListView) {
            setOnListScrollListener((AbsListView) v);
        } else if (v instanceof RecyclerView) {
            addOnRecyclerScrollListener((RecyclerView) v);
        }
    }

    abstract protected void addLoadMoreFooterView(View footerView);

    abstract public void addHeaderView(View v);

    abstract public void removeHeaderView(View v);

    private void createFooterView(Context context) {
        mLoadMoreFooterView = ReflectionUtil.newInst(AppEx.getListConfig().getFooterClz(), context);
        if (mLoadMoreFooterView == null) {
            // 加载失败, 使用默认的
            mLoadMoreFooterView = new DefaultFooter(context);
        }
        mLoadMoreFooterView.setOnRetryLoadClickListener(() -> {
            if (mListener != null) {
                return mListener.onManualLoadMore();
            }
            return false;
        });
    }

    /**
     * 是否正在加载更多
     *
     * @return
     */
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    /**
     * 停止加载更多, 重置footer
     *
     * @param isSucceed
     */
    public void stopLoadMore(boolean isSucceed) {
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            if (isSucceed) {
                mLoadMoreFooterView.changeStatus(IExtendStatus.normal);
            } else {
                mLoadMoreFooterView.changeStatus(IExtendStatus.failed);
            }
        }
    }

    /**
     * 开始底部load more
     */
    public void startLoadMore() {
        if (!mIsLoadingMore && mListener != null) {

            boolean result = mListener.onAutoLoadMore();
            if (result) {
                mIsLoadingMore = true;
                mLoadMoreFooterView.changeStatus(IExtendStatus.loading);
            } else {
                mLoadMoreFooterView.changeStatus(IExtendStatus.failed);
            }
        }
    }

    /**
     * 设置是否开启滑动底部自动加载更多
     *
     * @param enabled
     */
    public void setAutoLoadMoreEnabled(boolean enabled) {
        if (mAutoLoadMoreEnabled == enabled) {
            return;
        }

        mAutoLoadMoreEnabled = enabled;
        changeState(enabled);
    }

    public void setLoadMoreState(boolean state) {
        if (mAutoLoadMoreEnabled) {
            changeState(state);
        }

    }

    private void changeState(boolean state) {
        if (mCurrState == state) {
            return;
        }

        mCurrState = state;
        if (state) {
            mIsLoadingMore = false;
            mLoadMoreFooterView.show();
            mLoadMoreFooterView.changeStatus(IExtendStatus.normal);
            mLoadMoreFooterView.setOnClickListener(v -> startLoadMore());
        } else {
            mLoadMoreFooterView.hide();
            mLoadMoreFooterView.setOnClickListener(null);
        }
    }

    protected void addOnRecyclerScrollListener(RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !mIsLoadingMore && mAutoLoadMoreEnabled && mCurrState) {
                    RecyclerView.LayoutManager layoutManager = rv.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();

                    int firstVisibleItem = 0;
                    int[] firstVisibleInto = null;
                    if (layoutManager instanceof GridLayoutManager) {
                        firstVisibleItem = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        firstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        firstVisibleItem = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(firstVisibleInto)[0];
                    }

                    int total = visibleItemCount + firstVisibleItem;
                    if (total >= totalItemCount) {
                        startLoadMore();
                    }
                }

                if (mScrollListener != null) {
                    mScrollListener.onScrollStateChanged(rv, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy) {
                if (mScrollListener != null) {
                    mScrollListener.onScrolled(rv, dx, dy);
                }
            }
        });
    }

    protected void setOnListScrollListener(AbsListView lv) {
        lv.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mScrollListener != null) {
                    mScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mListener != null && !mIsLoadingMore && mAutoLoadMoreEnabled && mCurrState) {
                    int total = visibleItemCount + firstVisibleItem;
                    if (DeviceUtil.getBrand().equals(ConstantsEx.KBrandMeiZu)) {
                        // 单独处理魅族的手机, 他们计算的visibleItemCount就是比正常的少一个, 奇葩
                        if (total == totalItemCount - 1) {
                            startLoadMore();
                        }
                    } else {
                        if (total == totalItemCount) {
                            startLoadMore();
                        }
                    }
                }

                if (mScrollListener != null) {
                    mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
    }

    /**
     * 外部使用的监听
     *
     * @param listener
     */
    public void setOnScrollListener(OnScrollMixListener listener) {
        mScrollListener = listener;
    }

    public boolean isAutoLoadMoreEnabled() {
        return mAutoLoadMoreEnabled;
    }
}
