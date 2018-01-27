package jx.csp.ui.activity.share;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.constant.WatchPwdType;
import jx.csp.contact.WatchPwdContract;
import jx.csp.presenter.WatchPwdPresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseActivity;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;

/**
 * @auther ${HuoXuYu}
 * @since 2018/1/3
 */
@Route
public class WatchPwdActivity extends BaseActivity {
    private LinearLayout mLayoutNoPwd;
    private LinearLayout mLayoutExistingPwd;
    private EditText mEtPwd;
    private TextView mTvConfirm;
    private TextView mTvPwd;
    private TextView mTvTips;

    @Arg
    String mCourseId;

    private WatchPwdContract.V mView;
    private WatchPwdContract.P mPresenter;

    @Override
    public void initData() {
        mView = new WatchPwdViewImpl();
        mPresenter = new WatchPwdPresenterImpl(mView);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_watch_pwd;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.watch_setting_watch_pwd, this);
    }

    @Override
    public void findViews() {
        mLayoutNoPwd = findView(R.id.watch_pwd_layout_input_pwd);
        mLayoutExistingPwd = findView(R.id.watch_pwd_layout_pwd);
        mEtPwd = findView(R.id.watch_pwd_et_pwd);

        mTvConfirm = findView(R.id.watch_pwd_tv_confirm);
        mTvPwd = findView(R.id.watch_pwd_tv_pwd);
        mTvTips = findView(R.id.watch_pwd_tv_tips);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.watch_pwd_tv_confirm);
        setOnClickListener(R.id.watch_pwd_tv_create);

        //即时获取会议密码
        refresh(RefreshWay.embed);
        mPresenter.getPassword(WatchPwdType.getPwd, mCourseId, null);
        //密码输入格式
        getPwdChange();
    }

    @Override
    public boolean onRetryClick() {
        if (!super.onRetryClick()) {
            refresh(RefreshWay.embed);
            mPresenter.getPassword(WatchPwdType.getPwd, mCourseId, null);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.watch_pwd_tv_confirm: {
                refresh(RefreshWay.dialog);
                if (TextUtil.isNotEmpty(getPwd())) {
                    mPresenter.getPassword(WatchPwdType.setPwd, mCourseId, getPwd());
                } else {
                    mPresenter.getPassword(WatchPwdType.delete, mCourseId, mTvPwd.getText().toString());
                }
            }
            break;
            case R.id.watch_pwd_tv_create: {
                mEtPwd.setText(mPresenter.getRandomPwd(4));
                mEtPwd.setSelection(getPwd().length());
            }
            break;
        }
    }

    public String getPwd() {
        return mEtPwd.getText().toString();
    }

    /**
     * @return true为点击空白处收起键盘
     */
    @Override
    protected boolean enableHideKeyboardWhenChangeFocus() {
        return true;
    }

    /**
     * 密码输入格式
     */
    private void getPwdChange() {
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    mTvConfirm.setEnabled(true);
                } else {
                    mTvConfirm.setEnabled(false);
                }
            }
        });
    }

    private class WatchPwdViewImpl implements WatchPwdContract.V {

        @Override
        public void setExistingPwd() {
            goneView(mLayoutNoPwd);
            showView(mLayoutExistingPwd);

            mEtPwd.setText(ConstantsEx.KEmpty);
            mTvTips.setText(R.string.watch_tips2);
            mTvConfirm.setText(R.string.watch_delete_pwd);
            mTvConfirm.setEnabled(true);
        }

        @Override
        public void setUnPwd() {
            goneView(mLayoutExistingPwd);
            showView(mLayoutNoPwd);

            mTvPwd.setText(ConstantsEx.KEmpty);
            mTvTips.setText(R.string.watch_tips);
            mTvConfirm.setText(R.string.confirm);
            mTvConfirm.setEnabled(false);
        }

        @Override
        public void setPwdText(String password) {
            mTvPwd.setText(password);
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {
            WatchPwdActivity.this.setViewState(state);
        }
    }
}
