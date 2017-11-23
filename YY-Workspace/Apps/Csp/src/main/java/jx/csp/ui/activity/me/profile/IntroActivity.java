package jx.csp.ui.activity.me.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.IntroContract;
import jx.csp.presenter.IntroPresenterImpl;

/**
 * 个人简介
 *
 * @auther Huoxuyu
 * @since 2017/9/21
 */
@Route
public class IntroActivity extends BaseMyMessageActivity {

    private int KInfoCode = 1;
    private int KTextLength = 600;

    private EditText mEt;
    private TextView mTv;

    private IntroContract.P mIntroPresenter;
    private IntroContract.V mIntroView;

    @Override
    public void initData(Bundle state) {
        super.initData(state);

        mIntroView = new IntroViewImpl();
        mIntroPresenter = new IntroPresenterImpl(mIntroView);
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

        mIntroView.setIntroTextLength(KTextLength - getVal().length());
        mIntroPresenter.onTextChangedListener(getEt());
    }

    @Override
    protected EditText getEt() {
        return mEt;
    }

    @Override
    protected void doSet() {
        mPresenter.savePersonMessage(KInfoCode, mEt.getText().toString());
    }

    private class IntroViewImpl extends MyMessageViewImpl implements IntroContract.V {
        @Override
        public void setIntroTextLength(int length) {
            if (length > KTextLength) {
                length = 0;
            }
            mTv.setText(String.format(getString(R.string.my_message_intro_unit), length));
        }

        @Override
        public void onStopRefresh() {

        }

        @Override
        public void setViewState(int state) {

        }
    }
}
