package jx.csp.ui.activity.me.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import lib.ys.util.view.ViewUtil;

/**
 * 昵称
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class NickNameActivity extends BaseMyMessageActivity {

    private EditText mEt;
    private ImageView mIv;
    private int mLimit;

    private TextWatcher mWatcher;

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mLimit = getIntent().getIntExtra(Extra.KLimit, 18);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_nick_name;
    }

    @Override
    public void findViews() {
        mEt = findView(R.id.profile_et_name);
        mIv = findView(R.id.form_iv_clean);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnClickListener(R.id.form_iv_clean);

        addTextChangedListener(mEt, mIv);
        ViewUtil.limitInputCount(mEt, mLimit);

        mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mView.setNickNameTextListener(s, mEt, mWatcher);
            }
        };
        mEt.addTextChangedListener(mWatcher);
    }

    @Override
    public void onClick(View v) {
        mView.setClear(mEt);
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    protected void doSet() {
        mPresenter.savePersonMessage(KNickNameCode, mEt.getText().toString());
    }
}
