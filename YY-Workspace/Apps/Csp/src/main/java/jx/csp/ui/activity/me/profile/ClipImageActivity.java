package jx.csp.ui.activity.me.profile;

import android.widget.TextView;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.ClipImageContract;
import jx.csp.presenter.ClipImagePresenterImpl;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.BaseClipImageActivity;

/**
 * 裁剪图片
 *
 * @auther HuoXuYu
 * @since 2017/9/22
 */
@Route
public class ClipImageActivity extends BaseClipImageActivity {

    private ClipImageContract.P mPresenter;
    private ClipImageContract.V mView;

    @Override
    public void initData() {
        mView = new ClipImageViewImpl();
        mPresenter = new ClipImagePresenterImpl(mView);
    }

    @Override
    public void initNavBar(NavBar bar) {
        mView.setNavBar(bar);
    }

    @Override
    public void setViews() {
        super.setViews();

        mView.setNavBarTextColor();
    }

    @Override
    protected void afterClip() {
        refresh(RefreshWay.dialog);
        mPresenter.getupLoadAvatar(mBmp);
    }

    private class ClipImageViewImpl implements ClipImageContract.V {

        private TextView mTv;

        @Override
        public void setNavBar(NavBar bar) {
            bar.setBackgroundResource(R.color.white);
            Util.addBackIcon(bar, R.string.person_center_avatar, ClipImageActivity.this);
            mTv = bar.addTextViewRight(R.string.confirm, v -> clip());
        }

        @Override
        public void setNavBarTextColor() {
            mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
        }

        @Override
        public void setSuccessProcessed() {
            stopRefresh();
            setResult(RESULT_OK, getIntent());
            finish();
        }
    }
}
