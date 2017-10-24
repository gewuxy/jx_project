package jx.csp.ui.activity.me.profile;

import android.widget.TextView;

import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.model.Avatar;
import lib.yy.model.Avatar.TAvatar;
import lib.yy.network.Result;
import lib.yy.ui.activity.BaseClipImageActivity;

/**
 * 裁剪图片
 *
 * @auther HuoXuYu
 * @since 2017/9/22
 */
@Route
public class ClipImageActivity extends BaseClipImageActivity {

    private TextView mTv;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundResource(R.color.white);
        Util.addBackIcon(bar, R.string.person_center_avatar, this);
        mTv = bar.addTextViewRight(R.string.confirm, v -> clip());
    }

    @Override
    public void setViews() {
        super.setViews();

        mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
    }

    @Override
    protected void afterClip() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.upload(BmpUtil.toBytes(mBmp)).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Avatar.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Avatar> r = (Result<Avatar>) result;
        if (r.isSucceed()) {
            stopRefresh();

            Avatar avatar = r.getData();
            //头像路径保存到本地
            Profile.inst().update(Profile.inst().put(TProfile.avatar, avatar.getString(TAvatar.url)));
            Profile.inst().saveToSp();

            setResult(RESULT_OK, getIntent());
            finish();
            showToast(R.string.my_message_save_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }
}
