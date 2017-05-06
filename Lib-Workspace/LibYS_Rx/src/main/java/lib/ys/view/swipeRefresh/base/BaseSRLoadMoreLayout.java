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

import lib.ys.ConstantsEx;
import lib.ys.config.ListConfig;
import lib.ys.interfaces.OnRetryClickListener;
import lib.ys.util.DeviceUtil;
import lib.ys.util.ReflectionUtil;
import lib.ys.view.swipeRefresh.footer.BaseFooter;
import lib.ys.view.swipeRefresh.footer.DefaultFooter;
import lib.ys.view.swipeRefresh.interfaces.IExtend.TState;
import lib.ys.view.swipeRefresh.interfaces.ISRLoadMoreListener;
import lib.ys.widget.list.mix.MixOnScrollListener;

/**
 * @author yuansui
 */
abstract public class BaseSRLoadMoreLayout extends BaseSRLayout implements ISRLoadMoreListener {

    private BaseFooter mLoadMoreFooterView;

    private MixOnScrollListener mScrollListener;

    private boolean mIsLoadingMore;
    private boolean mEnableAutoLoadMore = true;

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

    private void createFooterView(Context context) {
        mLoadMoreFooterView = ReflectionUtil.newInst(ListConfig.getFooterClz(), context);
        if (mLoadMoreFooterView == null) {
            // 加载失败, 使用默认的
            mLoadMoreFooterView = new DefaultFooter(context);
        }
        mLoadMoreFooterView.setOnRetryLoadClickListener(new OnRetryClickListener() {

            @Override
            public boolean onRetryClick() {
                if (mListener != null) {
                    return mListener.onManualLoadMore();
                }
                return false;
            }
        });
    }

    @Override
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    @Override
    public void stopLoadMore() {
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            mLoadMoreFooterView.changeState(TState.normal);
        }
    }

    @Override
    public void startLoadMore() {
        if (!mIsLoadingMore && mListener != null) {

            boolean result = mListener.onAutoLoadMore();
            if (result) {
                mIsLoadingMore = true;
                mLoadMoreFooterView.changeState(TState.loading);
            } else {
                mLoadMoreFooterView.changeState(TState.failed);
            }
        }
    }

    @Override
    public void stopLoadMoreFailed() {
        if (mIsLoadingMore) {
            mIsLoadingMore = false;
            mLoadMoreFooterView.changeState(TState.failed);
        }
    }

    @Override
    public void setAutoLoadEnable(boolean enable) {
        if (mEnableAutoLoadMore == enable) {
            return;
        }

        mEnableAutoLoadMore = enable;
        if (!mEnableAutoLoadMore) {
            mLoadMoreFooterView.hide();
            mLoadMoreFooterView.setOnClickListener(null);
        } else {
            mIsLoadingMore = false;
            mLoadMoreFooterView.show();
            mLoadMoreFooterView.changeState(TState.normal);
            mLoadMoreFooterView.setOnClickListener(v -> startLoadMore());
        }
    }

    protected void addOnRecyclerScrollListener(RecyclerView rv) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && !mIsLoadingMore && mEnableAutoLoadMore) {
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
                if (mListener != null && !mIsLoadingMore && mEnableAutoLoadMore) {
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
    public void setOnScrollListener(MixOnScrollListener listener) {
        mScrollListener = listener;
    }
}
