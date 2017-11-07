package jx.csp.presenter;

import android.graphics.Bitmap;

import jx.csp.App;
import jx.csp.R;
import jx.csp.contact.ClipImageContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import lib.network.model.NetworkResp;
import lib.ys.util.bmp.BmpUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.model.Avatar;
import lib.yy.model.Avatar.TAvatar;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ClipImagePresenterImpl extends BasePresenterImpl<ClipImageContract.V> implements ClipImageContract.P {

    public ClipImagePresenterImpl(ClipImageContract.V v) {
        super(v);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Avatar.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Avatar> r = (Result<Avatar>) result;
        getView().onStopRefresh();
        if (r.isSucceed()) {
            Avatar avatar = r.getData();
            //头像路径保存到本地
            Profile.inst().update(Profile.inst().put(TProfile.avatar, avatar.getString(TAvatar.url)));
            Profile.inst().saveToSp();

            getView().setSuccessProcessed();
            App.showToast(R.string.my_message_save_success);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void upLoadAvatar(Bitmap bitmap) {
        exeNetworkReq(UserAPI.upload(BmpUtil.toBytes(bitmap)).build());
    }
}
