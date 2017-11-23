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
import lib.network.model.interfaces.IResult;
import lib.ys.util.bmp.BmpUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.model.Avatar;
import lib.yy.model.Avatar.TAvatar;

/**
 * @auther Huoxuyu
 * @since 2017/10/27
 */

public class ClipImagePresenterImpl extends BasePresenterImpl<ClipImageContract.V> implements ClipImageContract.P {

    public ClipImagePresenterImpl(ClipImageContract.V v) {
        super(v);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Avatar.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            Avatar avatar = (Avatar) r.getData();
            //头像路径保存到本地
            Profile.inst().update(Profile.inst().put(TProfile.avatar, avatar.getString(TAvatar.url)));
            Profile.inst().saveToSp();

            getView().setData();
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
