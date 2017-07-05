package yy.doctor.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import yy.doctor.R;

/**
 * 模块View
 *
 * @auther : GuoXuan
 * @since : 2017/6/19
 */
public class ModuleView extends LinearLayout {

    private final String mText; // 文字
    private final int mImageResId; // 图标

    private TextView mTextView;
    private ImageView mImageView;

    public ModuleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ModuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ModuleView);
        mText = ta.getString(R.styleable.ModuleView_module_text);
        mImageResId = ta.getResourceId(R.styleable.ModuleView_module_src, -1);
        ta.recycle();

        init();
    }

    private void init() {
        // 左边的图标
        View imageView = inflate(getContext(), R.layout.layout_module_view_image, null);
        mImageView = (ImageView) imageView.findViewById(R.id.module_view_iv);
        mImageView.setImageResource(mImageResId);
        addView(imageView);
        // 右边的文字
        View textView = inflate(getContext(), R.layout.layout_module_view_text, null);
        mTextView = (TextView) textView.findViewById(R.id.module_view_tv);
        mTextView.setText(mText);
        addView(textView);
    }

    public ModuleView setImage(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
        return this;
    }

    public ModuleView setTextColor(@ColorInt int color) {
        mTextView.setTextColor(color);
        return this;
    }
}
