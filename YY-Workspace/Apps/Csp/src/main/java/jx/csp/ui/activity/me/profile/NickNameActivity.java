package jx.csp.ui.activity.me.profile;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import inject.annotation.router.Route;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.contact.NickNameContract;
import jx.csp.presenter.NickNamePresenterImpl;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;

/**
 * 昵称
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class NickNameActivity extends BaseMyMessageActivity {

    private int KNickNameCode = 0;

    private EditText mEt;
    private ImageView mIv;
    private int mLimit;

    private NickNameContract.V mNickNameView;
    private NickNameContract.P mNickNamePresenter;

    @Override
    public void initData() {
        super.initData();
        mLimit = getIntent().getIntExtra(Extra.KLimit, 18);

        mNickNameView = new NickNameViewImpl();
        mNickNamePresenter = new NickNamePresenterImpl(mNickNameView);
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
        ViewUtil.limitInputCount(mEt, mLimit);

        mNickNamePresenter.onTextChangedListener(mEt);
        mNickNamePresenter.onTextBlankListener(mEt);
    }

    @Override
    public void onClick(View v) {
        mNickNameView.setTextClear();
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    protected void doSet() {
        mPresenter.savePersonMessage(KNickNameCode, mEt.getText().toString());
    }

    private class NickNameViewImpl extends MyMessageViewImpl implements NickNameContract.V {

        @Override
        public void setTextClear() {
            getEt().setText("");
        }

        @Override
        public void forbidInputBlank(String text) {
            getEt().setText(text);
            getEt().setSelection(text.length());
        }

        @Override
        public void buttonStatus() {
            mTv.setEnabled(true);
        }

        @Override
        public void setClearButton(String text) {
            if (mEt.hasFocus() && TextUtil.isNotEmpty(text)) {
                showView(mIv);
            } else {
                hideView(mIv);
            }
        }

        @Override
        public void onStopRefresh() {
        }

        @Override
        public void setViewState(int state) {
        }
    }
}
