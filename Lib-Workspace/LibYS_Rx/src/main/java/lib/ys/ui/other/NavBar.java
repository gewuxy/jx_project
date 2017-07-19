package lib.ys.ui.other;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.AppEx;
import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.config.NavBarConfig;
import lib.ys.fitter.DpFitter;
import lib.ys.util.UIUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;


/**
 * 导航栏
 * navigation bar
 */
public class NavBar extends RelativeLayout {

    private static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    private LinearLayout mLayoutLeft;
    private LinearLayout mLayoutMid;
    private LinearLayout mLayoutRight;

    private View mDivider;

    private static NavBarConfig mConfig;

    public NavBar(Context context) {
        super(context);
        nativeInit(true);
    }

    public NavBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        nativeInit(false);
    }

    private void nativeInit(boolean useCustomId) {
        if (useCustomId) {
            setId(R.id.nav_bar);
        }


        /**
         * 背景色, 先找背景图片, 再找背景颜色
         */
        @DrawableRes int bgRes = mConfig.getBgRes();
        if (bgRes != 0) {
            setBackgroundResource(bgRes);
        } else {
            @ColorRes int bgColorRes = mConfig.getBgColorRes();
            if (bgColorRes != 0) {
                setBackgroundColor(ResLoader.getColor(bgColorRes));
            }
        }

        View flatBar = null;
        if (AppEx.getConfig().isFlatBarEnabled()) {
            int heightPx = UIUtil.getStatusBarHeight(getContext()); // 已经转换完的高度px
            flatBar = ViewUtil.inflateSpaceViewPx(heightPx);
            flatBar.setId(R.id.flat_bar);
            // flatBar.setBackgroundColor(bgColor); // 不能设置颜色, 会重叠
            addView(flatBar, LayoutUtil.getRelativeParams(MATCH_PARENT, heightPx));
        }

        // 添加左边布局
        mLayoutLeft = new LinearLayout(getContext());
        mLayoutLeft.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams leftParams = getRelativeParams();
        leftParams.addRule(CENTER_VERTICAL);
        if (flatBar != null) {
            leftParams.addRule(BELOW, flatBar.getId());
        }
        addView(mLayoutLeft, leftParams);

        // 添加中间布局
        mLayoutMid = new LinearLayout(getContext());
        mLayoutMid.setId(R.id.nav_bar_mid);
        mLayoutMid.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams midParams = getRelativeParams();
        if (flatBar != null) {
            midParams.addRule(BELOW, flatBar.getId());
        }
        midParams.addRule(CENTER_IN_PARENT);
        addView(mLayoutMid, midParams);

        // 添加右边布局
        mLayoutRight = new LinearLayout(getContext());
        mLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams rightParams = getRelativeParams();
        rightParams.addRule(ALIGN_PARENT_RIGHT);
        rightParams.addRule(CENTER_VERTICAL);
        if (flatBar != null) {
            rightParams.addRule(BELOW, flatBar.getId());
        }
        addView(mLayoutRight, rightParams);

        mDivider = null;
        int dividerHeight = mConfig.getDividerHeight();
        if (dividerHeight != 0) {
            mDivider = ViewUtil.inflateSpaceViewPx(dividerHeight);
            mDivider.setBackgroundColor(mConfig.getDividerColorRes());
            mDivider.setId(R.id.nav_bar_divider);
            LayoutParams dividerParams = LayoutUtil.getRelativeParams(MATCH_PARENT, dividerHeight);
            dividerParams.addRule(ALIGN_BOTTOM, mLayoutMid.getId());
            addView(mDivider, dividerParams);
        }
    }

    public void setDividerColor(@ColorRes int id) {
        if (mDivider != null) {
            mDivider.setBackgroundColor(ResLoader.getColor(id));
        }
    }

    /**
     * 添加左边的图标
     *
     * @param id
     * @param lsn
     */
    public <T extends ViewGroup> T addViewLeft(@DrawableRes int id, OnClickListener lsn) {
        ViewGroup v = createImageView(id, lsn);
        if (v != null) {
            mLayoutLeft.addView(v, getLinearParams());
            show();
        }
        return (T) v;
    }

    /**
     * 添加左边的图标-带文字
     *
     * @param id
     * @param text
     * @param lsn
     */
    public <T extends ViewGroup> T addViewLeft(@DrawableRes int id, CharSequence text, OnClickListener lsn) {
        return addViewLeft(id, text, lsn, false);
    }

    /**
     * 添加左边的图标-带文字
     *
     * @param id
     * @param text
     * @param lsn
     * @param textClickable 文字是否也可以点击
     * @param <T>
     * @return
     */
    public <T extends ViewGroup> T addViewLeft(@DrawableRes int id, CharSequence text, OnClickListener lsn, boolean textClickable) {
        ViewGroup v = createImageView(id, text, lsn, textClickable);
        if (v != null) {
            mLayoutLeft.addView(v, getLinearParams());
            show();
        }
        return (T) v;
    }

    /**
     * 加入自定义布局-左对齐
     *
     * @param v
     * @param lsn
     */
    public <T extends View> T addViewLeft(View v, OnClickListener lsn) {
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }
        mLayoutLeft.addView(v, params);
        show();

        return (T) v;
    }

    /***
     * 添加右边图标
     *
     * @param id
     */
    public <T extends ViewGroup> T addViewRight(@DrawableRes int id, OnClickListener lsn) {
        ViewGroup v = createImageView(id, lsn);
        if (v != null) {
            mLayoutRight.addView(v, getLinearParams());
            show();
        }
        return (T) v;
    }

    /**
     * 加入自定义布局-右对齐
     *
     * @param v
     * @param lsn
     */
    public <T extends View> T addViewRight(View v, OnClickListener lsn) {
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }

        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(MATCH_PARENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.weight = 1;
        mLayoutRight.addView(v, params);
        show();

        return (T) v;
    }


    /**
     * 加入自定义布局-居中
     *
     * @param v
     */
    public void addViewMid(View v) {
        addViewMid(v, LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT), null);
    }

    /**
     * 加入自定义布局-居中
     *
     * @param v
     * @param lsn
     */
    public void addViewMid(View v, OnClickListener lsn) {
        addViewMid(v, LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT), lsn);
    }

    /**
     * 加入自定义布局-居中
     *
     * @param v
     * @param params 布局参数
     * @param lsn
     */
    public void addViewMid(View v, LinearLayout.LayoutParams params, OnClickListener lsn) {
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }

        params.gravity = Gravity.CENTER;
        mLayoutMid.addView(v, params);
        show();
    }

    /**
     * 获取带有点击背景色的iv
     *
     * @param id
     * @param lsn
     * @return
     */
    private <T extends ViewGroup> T createImageView(@DrawableRes int id, OnClickListener lsn) {
        return createImageView(id, null, lsn, false);
    }

    /**
     * 获取带有点击背景色的iv
     *
     * @param id
     * @param text
     * @param lsn
     * @return
     */
    private <T extends ViewGroup> T createImageView(@DrawableRes int id, CharSequence text, OnClickListener lsn, boolean textClickable) {
        if (id <= 0) {
            return null;
        }

        // 创建背景layout
        RelativeLayout layout = new RelativeLayout(getContext());
        int iconPadding = mConfig.getIconPaddingHorizontal();
        if (iconPadding != 0) {
            layout.setPadding(iconPadding, 0, iconPadding, 0);
        }

        // 设置点击背景色
        if (mConfig.getFocusBgColorRes() != 0) {
            StateListDrawable sd = new StateListDrawable();

            Drawable dNormal = new ColorDrawable(Color.TRANSPARENT);
            Drawable dPressed = new ColorDrawable(mConfig.getFocusBgColorRes());

            int pressed = android.R.attr.state_pressed;

            sd.addState(new int[]{pressed}, dPressed);
            sd.addState(new int[]{-pressed}, dNormal);

            ViewUtil.setBackground(layout, sd);
        } else if (mConfig.getFocusBgDrawableRes() != 0) {
            layout.setBackgroundResource(mConfig.getFocusBgDrawableRes());
        }

        // 创建image view
        ImageView iv = new ImageView(getContext());
        LayoutParams params = null;

        int iconSize = mConfig.getIconSize();
        if (iconSize != 0) {
            params = LayoutUtil.getRelativeParams(iconSize, iconSize);
        } else {
            params = LayoutUtil.getRelativeParams(WRAP_CONTENT, WRAP_CONTENT);
            iv.setScaleType(ScaleType.CENTER_INSIDE);
        }

        if (id != 0) {
            iv.setImageResource(id);
        }

        params.addRule(CENTER_IN_PARENT);
        layout.addView(iv, params);

        ViewGroup v = null;
        if (text != null) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.HORIZONTAL);
            l.setGravity(Gravity.CENTER);
            l.addView(layout, LayoutUtil.getLinearParams(WRAP_CONTENT, MATCH_PARENT));

            TextView tv = createTextView(mConfig.getTextSizeLeft(), mConfig.getTextColor(), 0, null);
            tv.setText(text);
            l.addView(tv, LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT));

            v = l;
        } else {
            v = layout;
        }

        if (lsn != null) {
            if (text != null && textClickable) {
                v.setOnClickListener(lsn);
            } else {
                layout.setOnClickListener(lsn);
            }
        }

        return (T) v;
    }

    /**
     * 根据高度获取linear params, 如无设置高度则为WRAP_CONTENT
     *
     * @return
     */
    private LinearLayout.LayoutParams getLinearParams() {
        int h = mConfig.getHeight();
        if (h == 0) {
            h = WRAP_CONTENT;
        } else {
            h = MATCH_PARENT;
        }
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, h);
        params.gravity = Gravity.CENTER;
        params.weight = 0;
        return params;
    }

    /**
     * 添加左中右布局专用, 其他地方不可以使用
     *
     * @return
     */
    private LayoutParams getRelativeParams() {
        int h = mConfig.getHeight();
        if (h == 0) {
            h = WRAP_CONTENT;
        }
        return LayoutUtil.getRelativeParams(WRAP_CONTENT, h);
    }

    /**
     * 根据属性创建TextView
     *
     * @param textSize
     * @param res
     * @param paddingHorizontal 左右的空隙
     * @param lsn
     * @return
     */
    private TextView createTextView(int textSize, @ColorRes int res, int paddingHorizontal, OnClickListener lsn) {
        TextView tv = new TextView(getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
//        tv.setEllipsize(TruncateAt.END);

        // 设置文字大小
        if (textSize != 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        // 设置文字颜色
        if (res != 0) {
            tv.setTextColor(ResLoader.getColorStateList(res));
        }

        if (paddingHorizontal != 0) {
            tv.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        }

        if (lsn != null) {
            tv.setOnClickListener(lsn);
        }

        return tv;
    }

    private LinearLayout.LayoutParams getTextLinearParams() {
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

	/* 设置TextView left相关********************************************** */

    public TextView addTextViewLeft(CharSequence text, OnClickListener lsn) {
        TextView tv = createTextView(mConfig.getTextSizeLeft(), mConfig.getTextColor(), mConfig.getTextMarginHorizontal(), lsn);
        setTvText(text, tv);
        mLayoutLeft.addView(tv, getTextLinearParams());
        return tv;
    }

    public TextView addTextViewLeft(int strResId, OnClickListener lsn) {
        return addTextViewLeft(getString(strResId), lsn);
    }

    /**
     * 设置右边文字
     *
     * @param strRes   R.string.xxx
     * @param colorRes R.color.xxx
     */
    public TextView addTextViewLeft(@StringRes int strRes, @ColorRes int colorRes, OnClickListener lsn) {
        return addTextViewLeft(getString(strRes), colorRes, lsn);
    }

    /**
     * 设置左边文字
     *
     * @param text     内容
     * @param colorRes R.color.xxx
     */
    public TextView addTextViewLeft(CharSequence text, @ColorRes int colorRes, OnClickListener lsn) {
        TextView tv = createTextView(mConfig.getTextSizeLeft(), colorRes, mConfig.getTextMarginHorizontal(), lsn);
        setTvText(text, tv);
        mLayoutLeft.addView(tv, getTextLinearParams());
        return tv;
    }

	/* 设置TextView mid相关********************************************** */

    /**
     * 添加中间的文字
     *
     * @param text 如果文本内容为null, 无效
     */
    public TextView addTextViewMid(CharSequence text) {
        return addTextViewMid(text, mConfig.getTextColor());
    }

    /**
     * 添加中间的文字
     *
     * @param text
     * @param id
     */
    public TextView addTextViewMid(CharSequence text, @ColorRes int id) {
        return addTextViewMid(text, id, 0);
    }

    /**
     * 添加中间的文字
     *
     * @param text
     * @param id
     * @param maxLength
     */
    public TextView addTextViewMid(CharSequence text, @ColorRes int id, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        TextView tv = createTextView(mConfig.getTextSizeMid(), id, 0, null);
        if (maxLength != 0) {
            tv.setMaxWidth((int) ((maxLength) * mConfig.getTextSizeMid() * DpFitter.getDensity()));
            tv.setSingleLine();
            tv.setEllipsize(TruncateAt.END);
        }
        setTvText(text, tv);
        mLayoutMid.addView(tv, getTextLinearParams());
        return tv;
    }

    /**
     * 添加中间的文字
     *
     * @param id
     */
    public TextView addTextViewMid(@StringRes int id) {
        return addTextViewMid(id, mConfig.getTextColor());
    }

    /**
     * 添加中间的文字
     *
     * @param strId
     * @param colorRes 自定义颜色
     */
    public TextView addTextViewMid(@StringRes int strId, @ColorRes int colorRes) {
        if (strId == 0) {
            return null;
        }
        return addTextViewMid(getString(strId), colorRes);
    }

	/* 设置TextView right相关********************************************** */

    public TextView addTextViewRight(CharSequence text, OnClickListener lsn) {
        TextView tv = createTextView(mConfig.getTextSizeRight(), mConfig.getTextColor(), mConfig.getTextMarginHorizontal(), lsn);
        setTvText(text, tv);
        mLayoutRight.addView(tv, getTextLinearParams());
        return tv;
    }

    public TextView addTextViewRight(@StringRes int id, OnClickListener lsn) {
        return addTextViewRight(getString(id), lsn);
    }

    /**
     * 设置右边文字
     *
     * @param strId    R.string.xxx
     * @param colorRes R.color.xxx
     */
    public TextView addTextViewRight(@StringRes int strId, @ColorRes int colorRes, OnClickListener lsn) {
        return addTextViewRight(getString(strId), colorRes, lsn);
    }

    /**
     * 设置右边文字
     *
     * @param text     内容
     * @param colorRes R.color.xxx
     */
    public TextView addTextViewRight(CharSequence text, @ColorRes int colorRes, OnClickListener lsn) {
        TextView tv = createTextView(mConfig.getTextSizeRight(), colorRes, mConfig.getTextMarginHorizontal(), lsn);
        setTvText(text, tv);
        mLayoutRight.addView(tv, getTextLinearParams());
        return tv;
    }

    /****************************
     * set title bar text tools
     */
    private void setTvText(CharSequence text, TextView tv) {
        if (!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        show();
    }

    public void gone() {
        ViewUtil.goneView(this);
    }

    public void show() {
        ViewUtil.showView(this);
    }

    private String getString(@StringRes int id) {
        return getContext().getString(id);
    }

    /*************************************
     * 以下是一些便捷集成工具
     ************************************* */

    /**
     * 给activity加入返回按钮及相应finish事件
     *
     * @param drawableId
     * @param act
     */
    public void addBackIcon(@DrawableRes int drawableId, final Activity act) {
        addBackIcon(drawableId, null, act);
    }

    /**
     * 给activity加入返回按钮及相应finish事件
     *
     * @param drawableId
     * @param text
     * @param act
     */
    public void addBackIcon(@DrawableRes int drawableId, CharSequence text, final Activity act) {
        addViewLeft(drawableId, text, v -> act.finish());
    }

    public View getLayoutMid() {
        return mLayoutMid;
    }

    public View getLayoutLeft() {
        return mLayoutLeft;
    }

    public View getLayoutRight() {
        return mLayoutRight;
    }

    public void setBackgroundAlpha(@IntRange(from = ConstantsEx.KAlphaMin, to = ConstantsEx.KAlphaMax) int alpha) {
        getBackground().setAlpha(alpha);
        if (mDivider != null) {
            mDivider.getBackground().setAlpha(alpha);
        }
    }

    /**
     * 使用{@link NavBarConfig}来初始化
     *
     * @param config
     */
    public static void init(NavBarConfig config) {
        mConfig = config;
    }

    public static NavBarConfig getConfig() {
        return mConfig;
    }

    /*******************************************
     * 以下是模糊代码, 还有bug暂时不能使用(由于图片上的问题缩放后在拖动的时候会抖动, 效果不好)
     */
    private View mBlurBgView;
    private Bitmap mBmpBlurred;
    private Bitmap mBmpToBlur;
    private Canvas mCanvasBlur;
    private static final float KScaleFactor = 8;

    public void setBlurBackground(View v) {
        mBlurBgView = v;
    }

    private boolean initBlur() {
        int w = mBlurBgView.getWidth();
        int h = mBlurBgView.getHeight();
        if (w == 0 || h == 0) {
            return false;
        }

        if (mCanvasBlur == null) {
            int scaleW = (int) (w / KScaleFactor);
            int scaleH = (int) (h / KScaleFactor);
            mBmpToBlur = Bitmap.createBitmap(scaleW, scaleH, Config.ARGB_8888);
            mCanvasBlur = BmpUtil.createCanvas(mBmpToBlur);
            mCanvasBlur.scale(1 / KScaleFactor, 1 / KScaleFactor);

//            float clipH = getHeight() / KScaleFactor;
//            clipH = clipH < mBmpToBlur.getHeight() ? clipH : mBmpToBlur.getHeight();
            mCanvasBlur.clipRect(0, 0, w, getHeight());
        }

        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBlurBgView != null) {
            if (initBlur()) {

//                canvas.drawColor(ParamsEx.getBgColor());

                mBlurBgView.draw(mCanvasBlur);

                mBmpBlurred = BmpUtil.blur(mBmpToBlur, 10, getContext());
//                mBmpBlurred = mBmpToBlur;

                int count = canvas.save();
                canvas.translate(mBlurBgView.getX(), mBlurBgView.getY());
                canvas.scale(KScaleFactor, KScaleFactor);
                canvas.drawBitmap(mBmpBlurred, 0, 0, null);
                canvas.restoreToCount(count);
            }
        }

        super.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBmpBlurred != null) {
            mBmpBlurred.recycle();
        }
        if (mBmpToBlur != mBmpBlurred && mBmpToBlur != null) {
            mBmpToBlur.recycle();
        }
    }


    /*****************************
     * 以下方法不能使用
     */

    /**
     * use {@link #addViewMid(View)} instead
     *
     * @param child
     */
    @Deprecated
    @Override
    public final void addView(View child) {
        super.addView(child);
    }
}
