package lib.ys.ui.interfaces.impl.scrollable;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import lib.ys.R;
import lib.ys.fitter.LayoutFitter;
import lib.ys.ui.interfaces.IScrollable;
import lib.ys.ui.interfaces.listener.scrollable.BaseOnScrollableListener;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;
import lib.ys.view.recycler.WrapRecyclerView;

/**
 * @auther yuansui
 * @since 2017/7/20
 */
abstract public class BaseScrollable<T> implements IScrollable<T> {

    private View mSv;

    private View mHeaderView;
    private View mFooterView;
    private RelativeLayout mEmptyView;

    public BaseScrollable(BaseOnScrollableListener<T> l) {
        if (l == null) {
            throw new IllegalStateException("OnScrollableListener can not be null");
        }
    }

    @Override
    public void findViews(@NonNull View contentView,
                          @IdRes int scrollableId,
                          @Nullable View header,
                          @Nullable View footer,
                          @Nullable View empty) {
        mSv = contentView.findViewById(scrollableId);
        if (mSv == null) {
            throw new NullPointerException("scrollable view can not be null");
        }

        LayoutInflater inflater = LayoutInflater.from(contentView.getContext());

        // 在这里添加header和footer, 以便于接着在子类里从header和footer里findview
        if (header != null) {
            mHeaderView = header;

            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            layout.addView(header, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(layout);
            nativeAddHeader(layout);
        }

        if (footer != null) {
            mFooterView = footer;

            RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.layout_list_extend, null);
            layout.addView(footer, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.WRAP_CONTENT));
            LayoutFitter.fit(layout);
            nativeAddFooter(layout);
        }

        // 添加empty view
        if (empty != null) {
            mEmptyView = (RelativeLayout) contentView.findViewById(R.id.list_empty_view);
            if (mEmptyView != null) {
                // 有可能布局没有保持要求的格式
                mEmptyView.addView(empty, LayoutUtil.getRelativeParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT));
            }
        }
    }

    private void nativeAddHeader(View v) {
        if (mSv instanceof ListView) {
            ((ListView) mSv).addHeaderView(v);
        } else if (mSv instanceof ExpandableListView) {
            ((ExpandableListView) mSv).addHeaderView(v);
        } else if (mSv instanceof WrapRecyclerView) {
            ((WrapRecyclerView) mSv).addHeaderView(v);
        } else if (mSv instanceof ScrollView) {

        }
    }

    private void nativeAddFooter(View v) {
        if (mSv instanceof ListView) {
            ((ListView) mSv).addFooterView(v);
        } else if (mSv instanceof ExpandableListView) {
            ((ExpandableListView) mSv).addFooterView(v);
        } else if (mSv instanceof WrapRecyclerView) {
            ((WrapRecyclerView) mSv).addFooterView(v);
        } else if (mSv instanceof ScrollView) {

        }
    }

    @Override
    public void addEmptyViewIfNonNull() {
        if (mEmptyView != null) {
            if (mSv instanceof AdapterView) {
                ((AdapterView) mSv).setEmptyView(mEmptyView);
            }
            // FIXME: RecyclerView暂不支持emptyView
        }
    }

    @Override
    public void addFooterView(View v) {
        if (mSv == null) {
            return;
        }

        if (mSv instanceof ListView) {
            ((ListView) mSv).addFooterView(v);
        } else if (mSv instanceof ExpandableListView) {
            ((ExpandableListView) mSv).addFooterView(v);
        } else if (mSv instanceof WrapRecyclerView) {
            ((WrapRecyclerView) mSv).addFooterView(v);
        }
    }

    @Override
    public void hideFooterView() {
        if (mFooterView != null) {
            ViewUtil.goneView(mFooterView);
        }
    }

    @Override
    public void showFooterView() {
        if (mFooterView != null) {
            ViewUtil.showView(mFooterView);
        }
    }

    @Override
    public void showHeaderView() {
        if (mHeaderView != null) {
            ViewUtil.showView(mHeaderView);
        }
    }

    @Override
    public void hideHeaderView() {
        if (mHeaderView != null) {
            ViewUtil.goneView(mHeaderView);
        }
    }

    @Nullable
    public final View getChildAt(int index) {
        if (mSv instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) mSv;
            if (index < vg.getChildCount()) {
                return vg.getChildAt(index);
            }
        }
        return null;
    }
}
