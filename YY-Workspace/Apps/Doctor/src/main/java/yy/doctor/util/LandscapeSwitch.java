package yy.doctor.util;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import lib.ys.fitter.DpFitter;
import lib.ys.util.AnimateUtil;
import lib.ys.util.view.LayoutUtil;

/**
 * 直播横屏管理
 *
 * @auther : GuoXuan
 * @since : 2017/10/15
 */
public class LandscapeSwitch implements View.OnTouchListener {

    private final String TAG = getClass().getSimpleName();

    private static final int KSmallW = 146;
    private static final int KSmallH = 96;

    private final int KDistanceX = 20;
    private final int KDistanceY = 20;
    private final int KDistance = 200; // 动画时长

    private int[] mLocation; // 父控件的位置信息

    private Point mPoint; // 记录按下坐标

    private boolean mScroll; // 是否在滑动
    private boolean mDispatch; // 是否可拦截(派送)

    private int mToX; // x轴的距离
    private int mToY; // y轴的距离

    private View mViewB;
    private View mViewS;

    private RelativeLayout.LayoutParams mParamB;
    private RelativeLayout.LayoutParams mParamS;

    // 修正值
    private int mXMax; // x轴最大值
    private int mXMin; // x轴最小值
    private int mYMax; // y轴最大值
    private int mYMin; // y轴最小值

    public static int[] getLocation(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    public void setDistanceXY(int distanceX, int distanceY) {
        mToX = distanceX;
        mToY = distanceY;
    }

    public void setDispatch(boolean state) {
        mDispatch = state;
    }

    public void initLandscape() {
        mViewB.setLayoutParams(mParamB);
        mViewS.setLayoutParams(mParamS);

        addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                if (mLocation == null) {
                    mLocation = new int[2];
                    ((ViewGroup) mViewB.getParent()).getLocationOnScreen(mLocation);
                    mXMin = mLocation[0] + mViewS.getWidth() / 2;
                    Log.d(TAG, "onTouch:mXMin " + mXMin);
                    mXMax = mLocation[0] + mViewB.getWidth() - mViewS.getWidth() / 2;
                    Log.d(TAG, "onTouch:mXMax " + mXMax);
                    mYMin = mLocation[1] + mViewS.getHeight() / 2;
                    Log.d(TAG, "onTouch:mYMin " + mYMin);
                    mYMax = mLocation[1] + mViewB.getHeight() - mViewS.getHeight() / 2;
                    Log.d(TAG, "onTouch:mYMax " + mYMax);
                }
                format(false, false, 0);

                removeOnGlobalLayoutListener(this);
            }

        });
    }

    public void setViewB(View v) {
        if (mViewB.equals(v)) {
            return;
        }
        mViewS = mViewB;
        mViewB = v;

    }

    public LandscapeSwitch(View view1, View view2) {
        mViewB = view1;
        mViewS = view2;

        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mToX = KDistanceX;
        mToY = KDistanceY;
        mScroll = false;
        mDispatch = false;
        mPoint = new Point();
        mParamB = LayoutUtil.getRelativeParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mParamS = LayoutUtil.getRelativeParams(DpFitter.dp(KSmallW), DpFitter.dp((KSmallH)));
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (mViewB.getId() == v.getId() || !mDispatch) {
            return false;
        }
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mPoint.x = (int) motionEvent.getRawX();
                mPoint.y = (int) motionEvent.getRawY();
                mScroll = false;
            }
            break;
            case MotionEvent.ACTION_MOVE: {

                float f1 = motionEvent.getRawX();
                float f2 = motionEvent.getRawY();

                // 保证不超出父控件
                if (f1 < mXMin) {
                    f1 = mXMin;
                }
                if (f1 > mXMax) {
                    f1 = mXMax;
                }
                if (f2 < mYMin) {
                    f2 = mYMin;
                }
                if (f2 > mYMax) {
                    f2 = mYMax;
                }

                if ((Math.abs(f1 - mPoint.x) > KDistanceX) || (Math.abs(f2 - mPoint.y) > KDistanceY)) {
                    AnimateUtil.translate(v, f1 - v.getWidth() / 2 - mLocation[0], f2 - v.getHeight() / 2 - mLocation[1], 0L);
                    mScroll = true;
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mScroll) {
                    boolean x = motionEvent.getRawX() < mLocation[0] + mViewB.getWidth() / 2;
                    boolean y = motionEvent.getRawY() < mLocation[1] + mViewB.getHeight() / 2;
                    format(x, y, KDistance);
                } else {
                    toggle();
                }
            }
            break;
        }

        return mScroll;
    }

    private void format(boolean horizontal, boolean vertical, long duration) {
        if (!mDispatch) {
            return;
        }
        int i;
        int j;
        if (horizontal) {
            i = mToX;
            if (vertical) {
                j = mToY;
                Log.d(TAG, "format: 左上, x = " + i + " , y = " + j);
            } else {
                j = mViewB.getHeight() - mViewS.getHeight() - mToY;
                Log.d(TAG, "format: 左下, x = " + i + " , y = " + j);
            }
        } else {
            i = mLocation[0] + mViewB.getWidth() - mViewS.getWidth() - mToX;
            if (vertical) {
                j = mToY;
                Log.d(TAG, "format: 右上, x = " + i + " , y = " + j);
            } else {
                j = mViewB.getHeight() - mViewS.getHeight() - mToY;
                Log.d(TAG, "format: 右下, x = " + i + " , y = " + j);
            }
        }
        mViewS.bringToFront();
        AnimateUtil.translate(mViewS, i, j, duration);

    }

    private void toggle() {
        // 切换图层
        mViewB.bringToFront();

        // 交换记录
        final View view = mViewB;
        mViewB = mViewS;
        mViewS = view;

        // 获取位置参数
        final int[] b = new int[2];
        ViewGroup.LayoutParams paramsB = mViewB.getLayoutParams();
        mViewB.getLocationOnScreen(b);
        final int[] s = new int[2];
        ViewGroup.LayoutParams paramsS = mViewS.getLayoutParams();
        mViewS.getLocationOnScreen(s);

        // 交换参数
        mViewB.setLayoutParams(paramsS);
        mViewS.setLayoutParams(paramsB);

        addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {
                AnimateUtil.translate(mViewB, s[0], s[1] - mLocation[1], 0);
                AnimateUtil.translate(mViewS, b[0], b[1] - mLocation[1], 0);
                removeOnGlobalLayoutListener(this);
            }

        });
    }

    private void addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (mViewB != null && mViewB.getViewTreeObserver().isAlive()) {
            mViewB.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        }
    }

    private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (mViewB != null && mViewB.getViewTreeObserver().isAlive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mViewB.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            } else {
                mViewB.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
            }
        }
    }

}