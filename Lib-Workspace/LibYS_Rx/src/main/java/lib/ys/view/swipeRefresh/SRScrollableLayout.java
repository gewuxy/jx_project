package lib.ys.view.swipeRefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import lib.ys.R;
import lib.ys.view.recycler.WrapRecyclerView;
import lib.ys.view.scrollableLayout.ScrollableHelper.ScrollableContainer;
import lib.ys.view.scrollableLayout.ScrollableLayout;
import lib.ys.view.swipeRefresh.base.BaseSRLoadMoreLayout;

/**
 * 整合ScrollableLayout, 只用于单个ListView/ScrollView/RecyclerView
 *
 * @author yuansui
 */
public class SRScrollableLayout extends BaseSRLoadMoreLayout<ScrollableLayout> implements ScrollableContainer {

    private View mScrollableView;
    private View mFooterWaitToAdd;

    public SRScrollableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScrollableLayout initContentView(Context context, AttributeSet attrs) {
        ScrollableLayout layout = new ScrollableLayout(context, attrs);
        layout.getHelper().setCurrentScrollableContainer(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setId(R.id.sr_scrollable_view);
        return layout;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getContentView().getChildCount() == 0) {
            List<View> views = new ArrayList<View>();

            // 先寻找需要修改的几个view, 不能直接同步删除和添加
            int count = getChildCount();
            for (int i = 0; i < count; ++i) {
                View v = getChildAt(i);
                if (v.equals(getContentView()) || v.equals(getHeader())) {
                    continue;
                }
                views.add(v);
            }

            for (int i = 0; i < views.size(); ++i) {
                View v = views.get(i);
                removeView(v);
                getContentView().addView(v);
            }

            if (mScrollableView == null) {
                mScrollableView = findScrollableView(getContentView());
                if (mScrollableView == null) {
                    throw new IllegalStateException("can not find AbsListView/ScrollView/RecyclerView");
                } else {
                    if (mScrollableView instanceof AbsListView) {
                        setOnListScrollListener((AbsListView) mScrollableView);
                        if (mScrollableView instanceof ListView && mFooterWaitToAdd != null) {
                            // footer view需要在这里添加才有效
                            ((ListView) mScrollableView).addFooterView(mFooterWaitToAdd);
                        }
                    } else if (mScrollableView instanceof WrapRecyclerView) {
                        addOnRecyclerScrollListener((WrapRecyclerView) mScrollableView);
                        if (mFooterWaitToAdd != null) {
                            ((WrapRecyclerView) mScrollableView).addFooterView(mFooterWaitToAdd);
                        }
                    }
                }
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 递归寻找ScrollableView, 支持AdapterView/ScrollView/RecyclerView
     *
     * @param viewGroup
     * @return
     */
    private View findScrollableView(ViewGroup viewGroup) {
        View ret = null;
        for (int i = 0; i < viewGroup.getChildCount(); ++i) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                ret = findScrollableView((ViewGroup) v);
                if (ret != null) {
                    break;
                }
            }
            if (v instanceof AbsListView) {
                ret = v;
                break;
            } else if (v instanceof ScrollView) {
                ret = v;
                break;
            } else if (v instanceof RecyclerView) {
                ret = v;
                break;
            }
        }
        return ret;
    }

    @Override
    protected void addLoadMoreFooterView(View footerView) {
        mFooterWaitToAdd = footerView;
    }

    @Override
    public void addHeaderView(View v) {
    }

    @Override
    public void removeHeaderView(View v) {
    }

    @Override
    public View getScrollableView() {
        return mScrollableView;
    }
}
