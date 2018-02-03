package jx.csp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import jx.csp.R;
import lib.ys.YSLog;

/**
 * @author CaiXiang
 * @since 2018/2/2
 */
public class ArcMenu extends ViewGroup implements OnClickListener {

    private static final String TAG = "ArcMenu";

    public enum Status {
        OPEN, CLOSE
    }

    public enum Position {
        LEFT_TOP, RIGHT_TOP, RIGHT_BOTTOM, LEFT_BOTTOM, CENTER_BOTTOM
    }

    /**
     * 菜单的显示位置
     */
    private Position mPosition = Position.LEFT_TOP;
    /**
     * 菜单显示的半径，默认100dp
     */
    private int mRadius = 100;
    /**
     * 用户点击的按钮
     */
    private View mButton;
    /**
     * 当前ArcMenu的状态
     */
    private Status mCurrentStatus = Status.CLOSE;

    private float mFromDegrees;
    private float mToDegrees;
    private int mBtnHeight;

    private StatusChange mStatusChange;
    /**
     * 回调接口
     */
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化属性
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ArcMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // dp convert to px
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mRadius, getResources().getDisplayMetrics());
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyle, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ArcMenu_position:
                    int val = a.getInt(attr, 0);
                    switch (val) {
                        case 0:
                            mPosition = Position.LEFT_TOP;
                            break;
                        case 1:
                            mPosition = Position.RIGHT_TOP;
                            break;
                        case 2:
                            mPosition = Position.RIGHT_BOTTOM;
                            break;
                        case 3:
                            mPosition = Position.LEFT_BOTTOM;
                            break;
                        case 4:
                            mPosition = Position.CENTER_BOTTOM;
                    }
                    break;
                case R.styleable.ArcMenu_radius:
                    mRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f,
                            getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ArcMenu_fromDegrees: {
                    mFromDegrees = a.getFloat(R.styleable.ArcMenu_fromDegrees, 0);
                }
                break;
                case R.styleable.ArcMenu_toDegrees: {
                    mToDegrees = a.getFloat(R.styleable.ArcMenu_toDegrees, 0);
                }
                break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutButton();
            int count = getChildCount();
            float degrees = mFromDegrees;
            float perDegree = (mToDegrees - mFromDegrees) / (count - 2);
            /**
             * 设置所有孩子的位置 例如(第一个为按钮)： 左上时，从左到右 ] 第2个：mRadius(sin0 , cos0)
             * 第3个：mRadius(sina ,cosa) 注：[a = Math.PI / 2 * (cCount - 1)]
             * 第4个：mRadius(sin2a ,cos2a) 第5个：mRadius(sin3a , cos3a) ...
             */
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(View.GONE);

                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
                // child view width
                int cWidth = child.getMeasuredWidth();
                // child view height
                int cHeight = child.getMeasuredHeight();
                if (mPosition == Position.LEFT_TOP) {
                    cl += 0;
                    ct += 0;
                }
                if (mPosition == Position.LEFT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                    cl += 0;
                }
                if (mPosition == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - cHeight - ct;
                    cl = getMeasuredWidth() - cWidth - cl;
                }
                if (mPosition == Position.RIGHT_TOP) {
                    cl = getMeasuredWidth() - cWidth - cl;
                    ct += 0;
                }
                if (mPosition == Position.CENTER_BOTTOM) {
                    if (mFromDegrees != 0 || mToDegrees != 0) {
                        cl = (int) Math.abs(mRadius * Math.cos(Math.toRadians(degrees)));
                        ct = (int) Math.abs(mRadius * Math.sin(Math.toRadians(degrees)));
                        YSLog.d("arc", "degrees = " + degrees);

                        if (degrees > 90 && degrees < 270) {
                            cl = getMeasuredWidth() / 2 - cWidth / 2 - cl;
                        } else {
                            cl = getMeasuredWidth() / 2 - cWidth / 2 + cl;
                        }

                        if (degrees == 270.0) {
                            ct = getMeasuredHeight() - cHeight / 2 - ct - mBtnHeight / 4;
                        } else {
                            ct = getMeasuredHeight() - cHeight / 2 - ct;
                        }
                        degrees += perDegree;
                    } else {
                        ct = (int) (mRadius * Math.sin(Math.PI / count * (i + 1)));
                        cl = (int) (mRadius * Math.cos(Math.PI / count * (i + 1)));

                        cl = getMeasuredWidth() / 2 - cWidth / 2 - cl;
                        ct = getMeasuredHeight() - cHeight - ct;
                    }
                }
                child.layout(cl, ct, cl + cWidth, ct + cHeight);
            }
        }
    }

    /**
     * 第一个子元素为按钮，为按钮布局且初始化点击事件
     */
    private void layoutButton() {
        View btn = getChildAt(0);
        btn.setOnClickListener(this);
        int l = 0;
        int t = 0;
        int width = btn.getMeasuredWidth();
        int height = btn.getMeasuredHeight();
        mBtnHeight = height;
        switch (mPosition) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
            case CENTER_BOTTOM:
                l = getMeasuredWidth() / 2 - width / 2;
                t = getMeasuredHeight() - height;
                break;
        }
        btn.layout(l, t, l + width, t + height);
    }

    /**
     * 为按钮添加点击事件
     */
    @Override
    public void onClick(View v) {
        if (mButton == null) {
            mButton = getChildAt(0);
        }
        toggleMenu(300);
    }

    public void toggleMenu(int durationMillis) {
        int count = getChildCount();

        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(View.VISIBLE);
            int xFlag = 1;
            int yFlag = 1;
            if (mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xFlag = -1;
            }
            if (mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yFlag = -1;
            }

            // child view width
            int cWidth = childView.getMeasuredWidth();
            // child view height
            int cHeight = childView.getMeasuredHeight();
            // child left
            int cl;
            // child top
            int ct;

            if (mPosition == Position.CENTER_BOTTOM) {
                if (mFromDegrees != 0 || mToDegrees != 0) {
                    cl = (int) childView.getX();
                    ct = (int) childView.getY();
                } else {
                    ct = (int) (mRadius * Math.sin(Math.PI / count * (i + 1)));
                    cl = (int) (mRadius * Math.cos(Math.PI / count * (i + 1)));

                    cl = getMeasuredWidth() / 2 - cWidth / 2 - cl;
                    ct = getMeasuredHeight() - cHeight - ct;
                }
            } else {
                cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
            }

            AnimationSet animSet = new AnimationSet(true);
            Animation transAnim = null;
            if (mCurrentStatus == Status.CLOSE) {
                // to open
                transAnim = new TranslateAnimation(
                        getMeasuredWidth() / 2 - cWidth / 2 - xFlag * cl, 0,
                        getMeasuredHeight() - mBtnHeight / 2 - yFlag * ct - cHeight / 2, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {
                // to close
                transAnim = new TranslateAnimation(
                        0, getMeasuredWidth() / 2 - cWidth / 2 - xFlag * cl,
                        0, getMeasuredHeight() - mBtnHeight / 2 - yFlag * ct - cHeight / 2);
                childView.setClickable(false);
                childView.setFocusable(false);
            }

            transAnim.setFillAfter(true);
            transAnim.setDuration(durationMillis);
            transAnim.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(View.GONE);
                    }
                }
            });

            // 为动画设置一个开始延迟时间，纯属好看，可以不设
            //transAnim.setStartOffset((i * 100) / (count - 1));
            RotateAnimation rotateAnim = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(durationMillis);
            rotateAnim.setFillAfter(true);
            animSet.addAnimation(rotateAnim);
            animSet.addAnimation(transAnim);
            childView.startAnimation(animSet);
            final int index = i + 1;
            childView.setOnClickListener(v -> {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onClick(childView, index - 1);
                    menuItemAnim(index - 1);
                    changeStatus();
                }
            });
        }
        changeStatus();
    }

    public void rotateView(Status mCurrentStatus) {
        if (mCurrentStatus == Status.OPEN) {
            rotateView(mButton, 45f, 0f, 300);
        } else {
            rotateView(mButton, 0f, 45f, 300);
        }
    }

    /**
     * 按钮的旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     * @param durationMillis
     */
    public static void rotateView(View view, float fromDegrees, float toDegrees, int durationMillis) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(durationMillis);
        rotate.setFillAfter(true);
        rotate.setInterpolator(new DecelerateInterpolator());
        view.startAnimation(rotate);
    }

    /**
     * 开始菜单动画，点击的MenuItem放大消失，其他的缩小消失
     *
     * @param item
     */
    private void menuItemAnim(int item) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View childView = getChildAt(i + 1);
            if (i == item) {
                childView.startAnimation(scaleBigAnim(300));
            } else {
                childView.startAnimation(scaleSmallAnim(300));
            }
            childView.setClickable(false);
            childView.setFocusable(false);
        }
    }

    /**
     * 缩小消失
     *
     * @param durationMillis
     * @return
     */
    private Animation scaleSmallAnim(int durationMillis) {
        Animation anim = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(durationMillis);
        anim.setFillAfter(true);
        return anim;
    }

    /**
     * 放大，透明度降低
     *
     * @param durationMillis
     * @return
     */
    private Animation scaleBigAnim(int durationMillis) {
        AnimationSet animationset = new AnimationSet(true);
        Animation anim = new ScaleAnimation(1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation alphaAnimation = new AlphaAnimation(1, 0);
        animationset.addAnimation(anim);
        animationset.addAnimation(alphaAnimation);
        animationset.setDuration(durationMillis);
        animationset.setFillAfter(true);
        return animationset;
    }

    private void changeStatus() {
        // 点击外面是否收起按钮
//        if (mCurrentStatus == Status.CLOSE) {
//            // 在arcMenu 要被打开的时候 给整个arcMenu 设置点击事件,
//            setOnClickListener(v -> ArcMenu.this.onClick(v));
//        } else {
//            setClickable(false);
//        }
        rotateView(mCurrentStatus);
        // 切换状态
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
        // 设置状态回调,给用户设置
        if (null != mStatusChange) {
            mStatusChange.arcMenuStatus(mCurrentStatus);
        }
    }

    public interface StatusChange {
        void arcMenuStatus(Status status);
    }

    public void setStatusChange(StatusChange statusChange) {
        mStatusChange = statusChange;
    }

    public void setmOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        mOnMenuItemClickListener = onMenuItemClickListener;
    }
}
