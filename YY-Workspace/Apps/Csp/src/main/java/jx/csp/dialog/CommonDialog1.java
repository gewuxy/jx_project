package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;

/**
 * @author : CaiXiang
 * @since : 2018/1/19
 */
public class CommonDialog1 extends CommonDialog {

    private TextView mTvTitle; // 标题
    private TextView mTvContent; // 内容

    public CommonDialog1(Context context) {
        super(context);
    }

    @Override
    public void setViews() {
        super.setViews();

        addHintView(View.inflate(getContext(), R.layout.dialog_have_title_and_content, null));
        mTvTitle = findView(R.id.dialog_tv_title); // 在dialog_have_title_and_content
        mTvContent = findView(R.id.dialog_tv_content); // 在dialog_have_title_and_content
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
