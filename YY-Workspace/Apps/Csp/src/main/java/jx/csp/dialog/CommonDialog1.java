package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import jx.csp.R;


/**
 *
 * @author : CaiXiang
 * @since : 2018/1/19
 */
public class CommonDialog1 extends CommonDialog {

    private TextView mTvTitle; // 标题
    private TextView mTvContent; // 内容

    public CommonDialog1(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_have_title_and_content;
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvTitle = findView(R.id.dialog_tv_title);
        mTvContent = findView(R.id.dialog_tv_content);
    }

    public void setTitle(CharSequence title) {
        mTvTitle.setText(title);
    }

    public void setTitle(@StringRes int id) {
        mTvTitle.setText(id);
    }

    public void setContent(CharSequence content) {
        mTvContent.setText(content);
    }

    public void setContent(@StringRes int id) {
        mTvContent.setText(id);
    }
}
