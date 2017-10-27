package jx.csp.presenter;

import android.graphics.Bitmap;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ClipImageContract;
import jx.csp.contact.ClipImageContract.V;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.network.model.NetworkResp;
import lib.ys.util.bmp.BmpUtil;
import lib.yy.model.Avatar;
import lib.yy.model.Avatar.TAvatar;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ClipImagePresenterImpl extends PresenterExImpl<ClipImageContract.V> implements ClipImageContract.P{

    private ClipImageContract.V mView;

    public ClipImagePresenterImpl(V v) {
        super(v);
        mView = v;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Avatar.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Avatar> r = (Result<Avatar>) result;
        if (r.isSucceed()) {

            Avatar avatar = r.getData();
            //头像路径保存到本地
            Profile.inst().update(Profile.inst().put(TProfile.avatar, avatar.getString(TAvatar.url)));
            Profile.inst().saveToSp();

            mView.setSuccessProcessed();
            App.showToast(R.string.my_message_save_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void getupLoadAvatar(Bitmap bitmap) {
        exeNetworkReq(UserAPI.upload(BmpUtil.toBytes(bitmap)).build());
    }
}
