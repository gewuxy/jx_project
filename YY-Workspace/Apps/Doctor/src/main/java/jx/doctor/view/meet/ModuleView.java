package jx.doctor.view.meet;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import jx.doctor.R;

/**
 * 单个模块View
 *
 * @auther : GuoXuan
 * @since : 2017/6/19
 */
public class ModuleView extends LinearLayout {

    private CharSequence mText; // 文字
    private int mImageResId; // 图标

    private TextView mTextView;
    private ImageView mImageView;

    public ModuleView(Context context) {
        super(context);

        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.drawable.meeting_module_selector_layout);

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

    public ModuleView setText(CharSequence text) {
        mText = text;
        if (mTextView != null) {
            mTextView.setText(text);
        }
        return this;
    }

    public ModuleView setImgResId(@DrawableRes int id) {
        mImageResId = id;
        if (mImageView != null) {
            mImageView.setImageResource(id);
        }
        return this;
    }
}
