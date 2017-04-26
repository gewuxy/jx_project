package lib.yy.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.State;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import lib.ys.util.res.ResLoader;

/**
 * 限用于YaYa医师
 *
 * @author zhy
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mOrientation;
    private int mRealHeight;

    @IntDef({
            Decoration.horizontal,
            Decoration.vertical,
            Decoration.hv_default,
    })
    public @interface Decoration {
        int horizontal = 0;
        int vertical = 1;
        int hv_default = 2;
    }


    public GridItemDecoration(@NonNull @Decoration int orientation, int height, @DrawableRes int resId) {
        mDivider = ResLoader.getDrawable(resId);
        mOrientation = orientation;
        mRealHeight = height;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, State state) {
        switch (mOrientation) {
            case Decoration.horizontal:
                drawHorizontal(c, parent);
                break;
            case Decoration.vertical:
                drawVertical(c, parent);
                break;
            case Decoration.hv_default:
                drawHorizontal(c, parent);
                drawVertical(c, parent);
            default:
                break;
        }
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        int halfHeight = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();
            if (bottom - top > mRealHeight) {
                if (i == 0) {
                    halfHeight = (bottom - top - mRealHeight) / 2;
                }
                top += halfHeight;
                bottom -= halfHeight;
            }
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
                                int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                // 如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {

                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;

                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (childCount % spanCount == 0) {
                childCount = childCount - spanCount;
            } else {
                childCount = childCount - childCount % spanCount;
            }

            // 如果是最后一行，则不需要绘制底部
            if (pos >= childCount) {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if (childCount % spanCount == 0) {
                    childCount = childCount - spanCount;
                } else {
                    childCount = childCount - childCount % spanCount;
                }

                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) {
                    return true;
                }
            } else {// StaggeredGridLayoutManager 且横向滚动

                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            // 如果是最后一行，则不需要绘制底部
            if (isLastColum(parent, itemPosition, spanCount, childCount))
            //同时还是最后一列
            {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
            // 如果是最后一列，则不需要绘制右边
            if (isLastRaw(parent, itemPosition, spanCount, childCount))
            //同时还是最后一行
            {
                outRect.set(0, 0, 0, 0);
            } else {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(),
                    mDivider.getIntrinsicHeight());
        }
    }
}
