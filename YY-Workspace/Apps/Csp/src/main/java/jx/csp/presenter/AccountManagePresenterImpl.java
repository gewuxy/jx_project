package jx.csp.presenter;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import jx.csp.App;
import jx.csp.Constants.LoginType;
import jx.csp.contact.AccountManageContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.AccountManageActivity.RelatedId;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class AccountManagePresenterImpl extends BasePresenterImpl<AccountManageContract.V> implements AccountManageContract.P {

    private String mNickName;

    public AccountManagePresenterImpl(AccountManageContract.V v) {
        super(v);
    }

    @Override
    public void unBindMobileAndEmail(int id, int type) {
        exeNetworkReq(id, UserAPI.unBind(type).build());
    }

    @Override
    public void BindThirdParty(int id, int thirdPartyId, String uniqueId, String nickName, String gender, String avatar) {
        //未绑定状态
        if (Util.noNetwork()) {
            return;
        }
        //绑定请求
        exeNetworkReq(id, UserAPI.bindAccountStatus()
                .thirdPartyId(thirdPartyId)
                .uniqueId(uniqueId)
                .nickName(nickName)
                .gender(gender)
                .avatar(avatar)
                .build());
        YSLog.d(TAG, "wwwww" + nickName);
    }

    @Override
    public void unBindThirdParty(int id, int thirdPartyId, String tips) {
        //已绑定, 解绑请求
        getView().confirmUnBindDialog(tips, v -> {
            if (Util.noNetwork()) {
                return;
            }
            exeNetworkReq(id, UserAPI.bindAccountStatus().thirdPartyId(thirdPartyId).build());
        });
    }

    @Override
    public void getPlatformAction(String platformName) {
        Platform sina = ShareSDK.getPlatform(platformName);

        sina.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                App.showToast("授权成功");
                PlatformDb platDB = platform.getDb();
                String userGender = platDB.getUserGender();
                String icon = platDB.getUserIcon();
                String userId = platDB.getUserId();
                mNickName = platDB.getUserName();
                BindThirdParty(RelatedId.bind_sina, RelatedId.bind_sina, userId, mNickName, userGender, icon);

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                getView().onStopRefresh();
                App.showToast("失败");

            }

            @Override
            public void onCancel(Platform platform, int i) {
                getView().onStopRefresh();
                App.showToast("取消");
            }
        });
        sina.SSOSetting(false);
        sina.authorize();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        getView().onStopRefresh();
        // 绑定的
        Result r = (Result) result;
        switch (id) {
            case RelatedId.bind_wx: {
                getView().unBindThirdParty(r, id);
            }
            break;
            case RelatedId.bind_sina: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.weibo_login))) {
                    if (r.isSucceed()) {
                        getView().bindPlatformNameSuccess(r, id, mNickName, LoginType.weibo_login);
                    }else {
                        onNetworkError(id, r.getError());
                    }
                } else {
                    getView().unBindThirdParty(r, id);
                }
            }
            break;
            case RelatedId.bind_jingxin: {
                getView().unBindThirdParty(r, id);
            }
            break;
            case RelatedId.bind_phone: {
                getView().unBind(r, id, TProfile.mobile);
            }
            break;
            case RelatedId.bind_email: {
                getView().unBind(r, id, TProfile.email);
            }
            break;
        }
    }
}
