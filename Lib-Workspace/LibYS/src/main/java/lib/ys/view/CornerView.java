package lib.ys.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.util.XmlAttrUtil;
import lib.ys.util.view.ViewUtil;


public class CornerView extends RelativeLayout {

    private static final int KDefaultRadiusDp = 5;
    private static final int KDefaultStrokeColor = Color.parseColor("#cccccc");

    private RectF mRoundRectContent;
    private RectF mRoundRectStroke;

    private Path mPathContent;
    private Path mPathStroke;

    private int mStrokeWidth;
    private float mRadius;
    private int mStrokeColor;

    private boolean mUseStroke;

    public CornerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.CornerView);
        mStrokeWidth = typeArray.getDimensionPixelOffset(R.styleable.CornerView_corner_strokeWidth, 1);
        mRadius = typeArray.getDimensionPixelOffset(R.styleable.CornerView_corner_radius, ConstantsEx.KInvalidValue);
        mStrokeColor = typeArray.getColor(R.styleable.CornerView_corner_strokeColor, KDefaultStrokeColor);
        mUseStroke = typeArray.getBoolean(R.styleable.CornerView_corner_useStroke, true);
        typeArray.recycle();

        // 宽默认就使用1px就好
        mStrokeWidth = XmlAttrUtil.convert(mStrokeWidth, 1);
        // increment corner radius to account for half pixels.
        mRadius = XmlAttrUtil.convert(mRadius, KDefaultRadiusDp) + .5f;

        ViewUtil.disableHardwareAcc(this, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (isInEditMode()) {
            return;
        }

        if (mPathContent != null) {
            return;
        }

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        if (w == 0 || h == 0) {
            return;
        }

        mPathContent = new Path();
        if (mUseStroke) {
            mRoundRectContent = new RectF(mStrokeWidth, mStrokeWidth, w - mStrokeWidth, h - mStrokeWidth);

            mRoundRectStroke = new RectF(0, 0, w, h);
            mPathStroke = new Path();
            mPathStroke.addRoundRect(mRoundRectStroke, mRadius, mRadius, Direction.CW);
        } else {
            mRoundRectContent = new RectF(0, 0, w, h);
        }
        mPathContent.addRoundRect(mRoundRectContent, mRadius, mRadius, Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.dispatchDraw(canvas);
            return;
        }

        int saveCount = 0;
        if (mUseStroke) {
            saveCount = canvas.save();
            canvas.clipPath(mPathStroke);
            canvas.drawColor(mStrokeColor);
            canvas.restoreToCount(saveCount);
        }

        saveCount = canvas.save();
        canvas.clipPath(mPathContent);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

}
