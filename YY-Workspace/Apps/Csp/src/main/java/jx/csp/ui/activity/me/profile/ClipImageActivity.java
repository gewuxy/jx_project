package jx.csp.ui.activity.me.profile;

import android.widget.TextView;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.contact.ClipImageContract;
import jx.csp.presenter.ClipImagePresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.BaseClipImageActivity;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;

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
        bar.setBackgroundResource(R.color.white);
        Util.addBackIcon(bar, R.string.person_center_avatar, this);
        TextView tv = bar.addTextViewRight(R.string.confirm, v -> clip());
        tv.setTextColor(ResLoader.getColor(R.color.text_167afe));
    }

    @Override
    protected void afterClip() {
        refresh(RefreshWay.dialog);
        mPresenter.upLoadAvatar(mBmp);
    }

    private class ClipImageViewImpl implements ClipImageContract.V {

        @Override
        public void setData() {
            setResult(RESULT_OK, getIntent());
            finish();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }
}
