package lib.ys.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 流式布局的RadioGroup
 */
public class GridRadioGroup extends NestRadioGroup {

    private int mColumn = 4;
    private int mGap;

    public GridRadioGroup(Context context) {
        super(context);
    }

    public GridRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int row = 0;

        if (childCount > 0) {
            View v = getChildAt(0);
            v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int w = v.getMeasuredWidth();
            int wR = maxWidth - w * mColumn;
            mGap = wR / mColumn;

            int h = v.getMeasuredHeight();
            row = mColumn / childCount;
            if (childCount % mColumn != 0) {
                row++;
            }
            y = row * h;
        }
        // 设置容器所需的宽度和高度
        setMeasuredDimension(maxWidth, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        int maxWidth = r - l;
        int x = 0;
        int y = 0;
        int row = 0;


        for (int i = 0; i < childCount; i++) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                x += width;
                x += mGap * i;

//                x += width;
//                y = row * height + height;
//                if (x > maxWidth) {
//                    x = width;
//                    row++;
//                    y = row * height + height;
//
//                }
//                if (x > maxWidth) {
//                    child.layout(0, y - height, maxWidth, y);
//                } else {
//                    child.layout(x - width, y - height, x, y);
//                }
            }
        }
    }
}