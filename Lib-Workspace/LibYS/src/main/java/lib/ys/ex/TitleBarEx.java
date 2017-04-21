package lib.ys.ex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.ys.ConstantsEx;
import lib.ys.R;
import lib.ys.config.AppConfig;
import lib.ys.config.TitleBarConfig;
import lib.ys.fitter.DpFitter;
import lib.ys.util.UIUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.ys.util.view.ViewUtil;

public class TitleBarEx extends RelativeLayout {

    private static final int MATCH_PARENT = LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = LayoutParams.WRAP_CONTENT;

    private Context mContext;

    private LinearLayout mLayoutLeft;
    private LinearLayout mLayoutMid;
    private LinearLayout mLayoutRight;

    private View mDivider;

    public TitleBarEx(Context context) {
        super(context);
        mContext = context;
        init(true);
    }

    public TitleBarEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(false);
    }

    private void init(boolean useCustomId) {
        if (useCustomId) {
            setId(R.id.title_bar);
        }

        /**
         * 背景色, 先找背景图片, 再找背景颜色
         */
        int bgDrawableId = TitleBarConfig.getBgDrawableId();
        if (bgDrawableId != 0) {
            setBackgroundResource(bgDrawableId);
        } else {
            int bgColor = TitleBarConfig.getBgColor();
            if (bgColor != 0) {
                setBackgroundColor(bgColor);
            }
        }

        View flatBar = null;
        if (AppConfig.isFlatBarEnabled()) {
            int heightPx = UIUtil.getStatusBarHeight(getContext()); // 已经转换完的高度px
            flatBar = ViewUtil.inflateSpaceViewPx(heightPx);
            flatBar.setId(R.id.flat_bar);
            // flatBar.setBackgroundColor(bgColor); // 不能设置颜色, 会重叠
            addView(flatBar, LayoutUtil.getRelativeParams(LayoutParams.MATCH_PARENT, heightPx));
        }

        // 添加左边布局
        mLayoutLeft = new LinearLayout(mContext);
        mLayoutLeft.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams leftParams = getRelativeParams();
        leftParams.addRule(CENTER_VERTICAL);
        if (flatBar != null) {
            leftParams.addRule(BELOW, flatBar.getId());
        }
        addView(mLayoutLeft, leftParams);

        // 添加中间布局
        mLayoutMid = new LinearLayout(mContext);
        mLayoutMid.setId(R.id.title_bar_mid);
        mLayoutMid.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams midParams = getRelativeParams();
        if (flatBar != null) {
            midParams.addRule(BELOW, flatBar.getId());
        }
        midParams.addRule(CENTER_IN_PARENT);
        addView(mLayoutMid, midParams);

        // 添加右边布局
        mLayoutRight = new LinearLayout(mContext);
        mLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams rightParams = getRelativeParams();
        rightParams.addRule(ALIGN_PARENT_RIGHT);
        rightParams.addRule(CENTER_VERTICAL);
        if (flatBar != null) {
            rightParams.addRule(BELOW, flatBar.getId());
        }
        addView(mLayoutRight, rightParams);

        mDivider = null;
        int dividerHeight = TitleBarConfig.getDividerHeightPx();
        if (dividerHeight != 0) {
            mDivider = ViewUtil.inflateSpaceViewPx(dividerHeight);
            mDivider.setBackgroundColor(TitleBarConfig.getDividerColor());
            mDivider.setId(R.id.title_divider);
            LayoutParams dividerParams = LayoutUtil.getRelativeParams(LayoutParams.MATCH_PARENT, dividerHeight);
            dividerParams.addRule(ALIGN_BOTTOM, mLayoutMid.getId());
            addView(mDivider, dividerParams);
        }
    }

    public void setDividerColor(@ColorInt int color) {
        if (mDivider != null) {
            mDivider.setBackgroundColor(color);
        }
    }

    /**
     * 添加左边的图标
     *
     * @param drawableId
     * @param lsn
     */
    public void addViewLeft(@DrawableRes int drawableId, OnClickListener lsn) {
        View v = getIvWithClickBgColor(drawableId, lsn);
        if (v != null) {
            mLayoutLeft.addView(v, getLinearParams());
            show();
        }
    }

    /**
     * 添加左边的图标-带文字
     *
     * @param drawableId
     * @param text
     * @param lsn
     */
    public void addViewLeft(@DrawableRes int drawableId, CharSequence text, OnClickListener lsn) {
        View v = getIvWithClickBgColor(drawableId, text, lsn);
        if (v != null) {
            mLayoutLeft.addView(v, getLinearParams());
            show();
        }
    }

    /**
     * 加入自定义布局-左对齐
     *
     * @param v
     * @param lsn
     */
    public void addViewLeft(View v, OnClickListener lsn) {
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }
        mLayoutLeft.addView(v, params);
        show();
    }

    /***
     * 添加右边图标
     *
     * @param drawableId
     */
    public View addViewRight(@DrawableRes int drawableId, OnClickListener lsn) {
        View v = getIvWithClickBgColor(drawableId, lsn);
        if (v != null) {
            mLayoutRight.addView(v, getLinearParams());
            show();
        }
        return v;
    }

    /**
     * 加入自定义布局-右对齐
     *
     * @param v
     * @param lsn
     */
    public void addViewRight(View v, OnClickListener lsn) {
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }
        mLayoutRight.addView(v, params);
        show();
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
        params.gravity = Gravity.CENTER;
        if (lsn != null) {
            v.setOnClickListener(lsn);
        }
        mLayoutMid.addView(v, params);
        show();
    }

    /**
     * 获取带有点击背景色的iv
     *
     * @param drawableId
     * @return
     */
    private View getIvWithClickBgColor(@DrawableRes int drawableId, OnClickListener lsn) {
        return getIvWithClickBgColor(drawableId, null, lsn);
    }

    /**
     * 获取带有点击背景色的iv
     *
     * @param drawableId
     * @return
     */
    private View getIvWithClickBgColor(@DrawableRes int drawableId, CharSequence text, OnClickListener lsn) {
        View v = null;
        if (drawableId == 0) {
            return v;
        }

        // 先创建背景layout
        RelativeLayout layout = new RelativeLayout(getContext());
        int iconPaddingDp = TitleBarConfig.getIconPaddingHorizontalDp();
        if (iconPaddingDp != 0) {
            int px = dpToPx(iconPaddingDp);
            layout.setPadding(px, 0, px, 0);
        }

        if (TitleBarConfig.getViewClickBgColor() != 0) {
            StateListDrawable sd = new StateListDrawable();

            Drawable dNormal = new ColorDrawable(Color.TRANSPARENT);
            Drawable dPressed = new ColorDrawable(TitleBarConfig.getViewClickBgColor());

            int pressed = android.R.attr.state_pressed;

            sd.addState(new int[]{pressed}, dPressed);
            sd.addState(new int[]{-pressed}, dNormal);

            ViewUtil.setBackground(layout, sd);
        }

        // 再创建image view
        ImageView iv = new ImageView(getContext());
        LayoutParams params = null;

        int iconSizeDp = TitleBarConfig.getIconSizeDp();
        if (iconSizeDp != 0) {
            int px = dpToPx(iconSizeDp);
            params = LayoutUtil.getRelativeParams(px, px);
        } else {
            params = LayoutUtil.getRelativeParams(WRAP_CONTENT, WRAP_CONTENT);
            iv.setScaleType(ScaleType.CENTER_INSIDE);
        }

        if (drawableId != 0) {
            iv.setImageResource(drawableId);
        }

        params.addRule(CENTER_IN_PARENT);
        layout.addView(iv, params);

        if (lsn != null) {
            layout.setOnClickListener(lsn);
        }

        if (text != null) {
            LinearLayout ll = new LinearLayout(getContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER);
            ll.addView(layout, LayoutUtil.getLinearParams(LayoutUtil.WRAP_CONTENT, LayoutUtil.MATCH_PARENT));

            TextView tv = getTvWithParams(TitleBarConfig.getTextSizeLeftDp(), TitleBarConfig.getTextColor(), 0, null);
            tv.setText(text);
            ll.addView(tv, LayoutUtil.getLinearParams(LayoutUtil.WRAP_CONTENT, LayoutUtil.WRAP_CONTENT));

            v = ll;
        } else {
            v = layout;
        }

        return v;
    }

    /**
     * 根据高度获取linear params, 如无设置高度则为WRAP_CONTENT
     *
     * @return
     */
    private LinearLayout.LayoutParams getLinearParams() {
        int height;
        int titleBarHeightDp = TitleBarConfig.getHeightDp();
        if (titleBarHeightDp == 0) {
            height = WRAP_CONTENT;
        } else {
            /**
             * 这里不能使用{@link UIUtil#dpToPx(float, Context)}
             * 因为在initTitleBar之前已经经历过fit流程了, 所以要根据父布局的大小决定
             */
            height = MATCH_PARENT;
        }
        LinearLayout.LayoutParams params = LayoutUtil.getLinearParams(WRAP_CONTENT, height);
        params.gravity = Gravity.CENTER;
        return params;
    }

    /**
     * 添加左中右布局专用, 其他地方不可以使用
     *
     * @return
     */
    private LayoutParams getRelativeParams() {
        int height;
        int titleBarHeightDp = TitleBarConfig.getHeightDp();
        if (titleBarHeightDp == 0) {
            height = WRAP_CONTENT;
        } else {
            // 这里要保持使用UiUtil的方法, 这个时候还没有经过fit流程
            height = UIUtil.dpToPx(titleBarHeightDp, mContext);
        }

        return LayoutUtil.getRelativeParams(WRAP_CONTENT, height);
    }

    /**
     * 根据属性创建TextView
     *
     * @param sizeDp
     * @param color
     * @param paddingHoriDp 左右的空隙
     * @param lsn
     * @return
     */
    private TextView getTvWithParams(int sizeDp, @ColorInt int color, int paddingHoriDp, OnClickListener lsn) {
        TextView tv = new TextView(mContext);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
//        tv.setEllipsize(TruncateAt.END);

        // 设置文字大小
        if (sizeDp != 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(sizeDp));
        }

        // 设置文字颜色
        if (color != 0) {
            tv.setTextColor(color);
        }

        if (paddingHoriDp != 0) {
            int px = dpToPx(paddingHoriDp);
            tv.setPadding(px, 0, px, 0);
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
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeLeftDp(), TitleBarConfig.getTextColor(), TitleBarConfig.getTextMarginHorizontalDp(), lsn);
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
     * @param strResId    R.string.xxx
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewLeft(@StringRes int strResId, @ColorRes int colorResId, boolean isStateList, OnClickListener lsn) {
        return addTextViewLeft(getString(strResId), colorResId, isStateList, lsn);
    }

    /**
     * 设置右边文字
     *
     * @param text        内容
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewLeft(CharSequence text, @ColorRes int colorResId, boolean isStateList, OnClickListener lsn) {
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeLeftDp(), 0, TitleBarConfig.getTextMarginHorizontalDp(), lsn);
        setTvTextWithColor(tv, text, colorResId, isStateList);
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
        return addTextViewMid(text, TitleBarConfig.getTextColor());
    }

    /**
     * 添加中间的文字
     *
     * @param text
     * @param textColor
     */
    public TextView addTextViewMid(CharSequence text, @ColorInt int textColor) {
        return addTextViewMid(text, textColor, 0);
    }

    /**
     * 添加中间的文字
     *
     * @param text
     * @param textColor
     * @param maxLength
     */
    public TextView addTextViewMid(CharSequence text, @ColorInt int textColor, int maxLength) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeMidDp(), textColor, 0, null);
        if (maxLength != 0) {
            tv.setMaxWidth((int) ((maxLength) * TitleBarConfig.getTextSizeMidDp() * DpFitter.getDensity()));
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
     * @param strResId
     */
    public TextView addTextViewMid(int strResId) {
        return addTextViewMid(strResId, TitleBarConfig.getTextColor());
    }

    /**
     * 添加中间的文字
     *
     * @param strResId
     * @param textColor 自定义颜色
     */
    public TextView addTextViewMid(@StringRes int strResId, @ColorInt int textColor) {
        if (strResId == 0) {
            return null;
        }
        return addTextViewMid(getString(strResId), textColor);
    }

    /**
     * 设置中间文字
     *
     * @param strResId    R.string.xxx
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewMid(@StringRes int strResId, @ColorRes @XmlRes int colorResId, boolean isStateList) {
        return addTextViewMid(getString(strResId), colorResId, isStateList);
    }

    /**
     * 设置中间文字
     *
     * @param text        内容
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewMid(CharSequence text, @ColorRes @XmlRes int colorResId, boolean isStateList) {
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeMidDp(), 0, 0, null);
        setTvTextWithColor(tv, text, colorResId, isStateList);
        mLayoutMid.addView(tv, getTextLinearParams());
        return tv;
    }

	/* 设置TextView right相关********************************************** */

    public TextView addTextViewRight(CharSequence text, OnClickListener lsn) {
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeRightDp(), TitleBarConfig.getTextColor(), TitleBarConfig.getTextMarginHorizontalDp(), lsn);
        setTvText(text, tv);
        mLayoutRight.addView(tv, getTextLinearParams());
        return tv;
    }

    public TextView addTextViewRight(@StringRes int strResId, OnClickListener lsn) {
        return addTextViewRight(getString(strResId), lsn);
    }

    /**
     * 设置右边文字
     *
     * @param strResId    R.string.xxx
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewRight(@StringRes int strResId, @ColorRes @XmlRes int colorResId, boolean isStateList, OnClickListener lsn) {
        return addTextViewRight(getString(strResId), colorResId, isStateList, lsn);
    }

    /**
     * 设置右边文字
     *
     * @param text        内容
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    public TextView addTextViewRight(CharSequence text, @ColorRes @XmlRes int colorResId, boolean isStateList, OnClickListener lsn) {
        TextView tv = getTvWithParams(TitleBarConfig.getTextSizeRightDp(), 0, TitleBarConfig.getTextMarginHorizontalDp(), lsn);
        setTvTextWithColor(tv, text, colorResId, isStateList);
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

    /**
     * 设置Textview字体内容及颜色
     *
     * @param text        内容
     * @param colorResId  R.color.xxx
     * @param isStateList 是否为带有消息判断的ColorStateList
     */
    private void setTvTextWithColor(TextView tv, CharSequence text, @ColorRes int colorResId, boolean isStateList) {
        setTvText(text, tv);
        setTvColor(tv, colorResId, isStateList);
    }

    private void setTvColor(TextView tv, @ColorRes int colorResId, boolean isStateList) {
        if (isStateList) {
            tv.setTextColor(ResLoader.getColorStateList(colorResId));
        } else {
            tv.setTextColor(ResLoader.getColor(colorResId));
        }
    }

    public void gone() {
        ViewUtil.goneView(this);
    }

    public void show() {
        ViewUtil.showView(this);
    }

    private String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    /*************************************
     * 以下是一些便捷集成工具
     ************************************* */

    /**
     * 给activity的titlebar加入返回按钮及相应finish事件
     *
     * @param drawableId
     * @param act
     */
    public void addBackIcon(@DrawableRes int drawableId, final Activity act) {
        addBackIcon(drawableId, null, act);
    }

    /**
     * 给activity的titlebar加入返回按钮及相应finish事件
     *
     * @param drawableId
     * @param text
     * @param act
     */
    public void addBackIcon(@DrawableRes int drawableId, CharSequence text, final Activity act) {
        addViewLeft(drawableId, text, new OnClickListener() {

            @Override
            public void onClick(View v) {
                act.finish();
            }
        });
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

    private int dpToPx(float dp) {
        return DpFitter.dp(dp);
    }

    public void setBackgroundAlpha(@IntRange(from = ConstantsEx.KAlphaMin, to = ConstantsEx.KAlphaMax) int alpha) {
        getBackground().setAlpha(alpha);
        if (mDivider != null) {
            mDivider.getBackground().setAlpha(alpha);
        }
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

//                canvas.drawColor(ParamsEx.getAppBgColor());

                mBlurBgView.draw(mCanvasBlur);

                mBmpBlurred = BmpUtil.fastBlur(mBmpToBlur, 10);
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
