package jx.csp.presenter;

import java.util.ArrayList;
import java.util.List;

import jx.csp.App;
import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.contact.AccountManageContract;
import jx.csp.model.BindInfoList;
import jx.csp.model.BindInfoList.TBindInfo;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.ConstantsEx;
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

public class AccountManagePresenterImpl extends BasePresenterImpl<AccountManageContract.V>
        implements AccountManageContract.P {

    private String mNickName;

    public AccountManagePresenterImpl(AccountManageContract.V v) {
        super(v);
    }

    @Override
    public void doAuth(Type type, int id) {
        Platform.auth(type, new OnAuthListener() {

            @Override
            public void onAuthSuccess(AuthParams params) {
                String userGender = params.getGender();
                String icon = params.getIcon();
                String userId = params.getId();
                mNickName = params.getName();

                bindThirdParty(id, id, userId, mNickName, userGender, icon);

                App.showToast("授权成功");
            }

            @Override
            public void onAuthError(String message) {
                getView().onStopRefresh();
                App.showToast("失败: " + message);
            }

            @Override
            public void onAuthCancel() {
                getView().onStopRefresh();
                App.showToast("取消");
            }
        });
    }

    @Override
    public void bindThirdParty(int id, int thirdPartyId, String uniqueId, String nickName, String gender, String avatar) {
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
        YSLog.d(TAG, "www = " + nickName);
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

        switch (bindType) {
            case LoginType.wechat: {
                Notifier.inst().notify(NotifyType.bind_wx, Profile.inst().getBindNickName(LoginType.wechat));
            }
            break;
            case LoginType.sina: {
                Notifier.inst().notify(NotifyType.bind_sina, Profile.inst().getBindNickName(LoginType.sina));
            }
            break;
            case LoginType.facebook: {
                Notifier.inst().notify(NotifyType.bind_fackbook, Profile.inst().getBindNickName(LoginType.facebook));
            }
            break;
            case LoginType.twitter: {
                Notifier.inst().notify(NotifyType.bind_twitter, Profile.inst().getBindNickName(LoginType.twitter));
            }
            break;
        }
    }

    @Override
    public void unBindMobileOrEmailReq(int id, int type) {
        exeNetworkReq(id, UserAPI.unBind(type).build());
    }

    @Override
    public void unBindThirdPartyReq(int id, int thirdPartyId, String tips) {
        //已绑定, 解绑请求
        getView().confirmUnBindDialog(tips, v -> {
            if (Util.noNetwork()) {
                return;
            }
            exeNetworkReq(id, UserAPI.bindAccountStatus().thirdPartyId(thirdPartyId).build());
        });
    }

    @Override
    public void unBindEmailOrMobileSuccess(Result r, int id, TProfile key) {
        if (r.isSucceed()) {
            App.showToast(R.string.account_unbind_succeed);
            getView().unBindRefreshItem(id);

            Profile.inst().put(key, ConstantsEx.KEmpty);
            Profile.inst().saveToSp();
            Notifier.inst().notify(NotifyType.profile_change);
        } else {
            App.showToast(r.getMessage());
        }
    }

    @Override
    public void unBindThirdPartySuccess(Result r, int id) {
        if (r.isSucceed()) {
            App.showToast(R.string.account_unbind_succeed);

            List<BindInfoList> infoList = Profile.inst().getList(TProfile.bindInfoList);
            boolean flag = true;
            for (BindInfoList list : infoList) {
                if (list.getInt(TBindInfo.thirdPartyId) == id) {
                    list.clear();
                    flag = false;
                }
            }
            if (flag) {
                BindInfoList bindInfoList = new BindInfoList();
                bindInfoList.put(TBindInfo.thirdPartyId, ConstantsEx.KEmpty);
                bindInfoList.put(TBindInfo.nickName, ConstantsEx.KEmpty);
                infoList.add(bindInfoList);
            }

            getView().unBindRefreshItem(id);
            Profile.inst().put(TProfile.bindInfoList, infoList);
            Profile.inst().saveToSp();
        } else {
            App.showToast(r.getMessage());
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
            case LoginType.wechat: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.wechat))) {
                    if (r.isSucceed()) {
                        setSaveThirdPartyNickName(r, id, mNickName, LoginType.wechat);
                    } else {
                        onNetworkError(id, r.getError());
                    }
                } else {
                    unBindThirdPartySuccess(r, id);
                }
            }
            break;
            case LoginType.sina: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.sina))) {
                    if (r.isSucceed()) {
                        setSaveThirdPartyNickName(r, id, mNickName, LoginType.sina);
                    } else {
                        onNetworkError(id, r.getError());
                    }
                } else {
                    unBindThirdPartySuccess(r, id);
                }
            }
            break;
            case LoginType.yaya: {
                unBindThirdPartySuccess(r, id);
            }
            break;
            case LoginType.phone: {
                unBindEmailOrMobileSuccess(r, id, TProfile.mobile);
            }
            break;
            case LoginType.email: {
                unBindEmailOrMobileSuccess(r, id, TProfile.email);
            }
            break;
        }
    }
}
