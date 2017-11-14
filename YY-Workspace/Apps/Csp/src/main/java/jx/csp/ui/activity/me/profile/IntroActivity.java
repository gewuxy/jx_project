package jx.csp.ui.activity.me.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Route;
import jx.csp.R;

/**
 * 个人简介
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class IntroActivity extends BaseMyMessageActivity {

    private EditText mEt;
    private TextView mTv;

    @Override
    public void initData(Bundle state) {
        super.initData(state);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_intro;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.intro_et);
        mTv = findView(R.id.intro_tv_num);
    }

    @Override
    public void setViews() {
        super.setViews();

        mView.setIntroTextLength(KTextLength - getVal().length(), mTv);

        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mView.setIntroChangedTextLength(s, mTv);
            }
        });
    }


    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    protected void doSet() {
        mPresenter.savePersonMessage(KInfoCode, mEt.getText().toString());
    }
}
