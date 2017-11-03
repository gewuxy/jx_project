package jx.csp.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import jx.csp.App;
import jx.csp.Constants.LoginType;
import jx.csp.contact.AccountManageContract;
import jx.csp.model.BindInfoList;
import jx.csp.model.BindInfoList.TBindInfo;
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
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;

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
    public void setSaveThirdPartyNickName(Result r, int id, String nickName, int bindType) {
        List<BindInfoList> infoList = Profile.inst().getList(TProfile.bindInfoList);
        if (infoList == null) {
            infoList = new ArrayList<>();
        }

        boolean flag = true;
        for (BindInfoList list : infoList) {
            if (list.getInt(TBindInfo.thirdPartyId) == bindType) {
                list.put(TBindInfo.nickName, nickName);
                flag = false;
            }
        }

        if (flag) {
            BindInfoList bindInfoList = new BindInfoList();
            bindInfoList.put(TBindInfo.thirdPartyId, bindType);
            bindInfoList.put(TBindInfo.nickName, nickName);
            infoList.add(bindInfoList);
        }

        Profile.inst().put(TProfile.bindInfoList, infoList);
        Profile.inst().saveToSp();

        if (bindType == LoginType.weibo_login) {
            Notifier.inst().notify(NotifyType.bind_sina, Profile.inst().getBindNickName(LoginType.weibo_login));
        } else {
            Notifier.inst().notify(NotifyType.bind_wx, Profile.inst().getBindNickName(LoginType.wechat_login));
        }
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
                        setSaveThirdPartyNickName(r, id, mNickName, LoginType.weibo_login);
                    } else {
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
