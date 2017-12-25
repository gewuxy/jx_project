package jx.csp.presenter;

import java.util.ArrayList;
import java.util.List;

import jx.csp.App;
import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.contact.AccountManageContract;
import jx.csp.model.BindInfo;
import jx.csp.model.BindInfo.TBindInfo;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.BaseAccountActivity.RelatedId;
import jx.csp.util.Util;
import lib.jx.contract.BasePresenterImpl;
import lib.jx.notify.Notifier;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.interfaces.IResult;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnAuthListener;
import lib.platform.model.AuthParams;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;

/**
 * @auther HuoXuYu
 * @since 2017/10/26
 */

public class AccountManagePresenterImpl extends BasePresenterImpl<AccountManageContract.V>
        implements AccountManageContract.P {

    private String mNickName;

    public AccountManagePresenterImpl(AccountManageContract.V v) {
        super(v);
    }

    @Override
    public void auth(int bindId, Type type) {
        getView().onStopRefresh();
        Platform.auth(type, new OnAuthListener() {

            @Override
            public void onAuthSuccess(AuthParams params) {
                String userGender = params.getGender();
                String icon = params.getIcon();
                String userId = params.getId();
                String unionId = params.getUnionId();
                mNickName = params.getName();

                if (bindId == BindId.wechat) {
                    bind(bindId, unionId, mNickName, userGender, icon);
                } else {
                    bind(bindId, userId, mNickName, userGender, icon);
                }

                App.showToast(R.string.account_authorization_success);
            }

            @Override
            public void onAuthError(String message) {
                getView().onStopRefresh();
                App.showToast(R.string.auth_fail + message);
            }

            @Override
            public void onAuthCancel() {
                getView().onStopRefresh();
            }
        });
    }

    @Override
    public void bind(int bindId, String uniqueId, String nickName, String gender, String avatar) {
        //未绑定状态
        if (Util.noNetwork()) {
            return;
        }
        //绑定请求
        exeNetworkReq(bindId, UserAPI.bindAccountStatus()
                .thirdPartyId(bindId)
                .uniqueId(uniqueId)
                .nickName(nickName)
                .gender(gender)
                .avatar(avatar)
                .build());
        YSLog.d(TAG, "nickName = " + nickName);
    }

    @Override
    public void unBind(int bindId) {
        switch (bindId) {
            case BindId.email:
            case BindId.phone: {
                exeNetworkReq(bindId, UserAPI.unBind(bindId).build());
            }
            break;
            default: {
                exeNetworkReq(bindId, UserAPI.bindAccountStatus().thirdPartyId(bindId).build());
            }
            break;
        }
    }

    @Override
    public void saveNickName(@BindId int bindId, String nickName) {
        List<BindInfo> infoList = Profile.inst().getList(TProfile.bindInfoList);
        if (infoList == null) {
            infoList = new ArrayList<>();
        }

        boolean flag = true;
        for (BindInfo info : infoList) {
            if (info.getInt(TBindInfo.thirdPartyId) == bindId) {
                info.put(TBindInfo.nickName, nickName);
                flag = false;
            }
        }

        if (flag) {
            BindInfo bindInfo = new BindInfo();
            bindInfo.put(TBindInfo.thirdPartyId, bindId);
            bindInfo.put(TBindInfo.nickName, nickName);
            infoList.add(bindInfo);
        }

        Profile.inst().put(TProfile.bindInfoList, infoList);
        Profile.inst().saveToSp();

        switch (bindId) {
            case BindId.wechat: {
                Notifier.inst().notify(NotifyType.bind_wx, Profile.inst().getBindNickName(BindId.wechat));
            }
            break;
            case BindId.sina: {
                Notifier.inst().notify(NotifyType.bind_sina, Profile.inst().getBindNickName(BindId.sina));
            }
            break;
            case BindId.facebook: {
                Notifier.inst().notify(NotifyType.bind_fackbook, Profile.inst().getBindNickName(BindId.facebook));
            }
            break;
            case BindId.twitter: {
                Notifier.inst().notify(NotifyType.bind_twitter, Profile.inst().getBindNickName(BindId.twitter));
            }
            break;
        }
    }

    @Override
    public void onUnBindSuccess(@RelatedId int id, IResult r, TProfile key) {
        App.showToast(R.string.account_unbind_succeed);
        getView().refreshItem(id);

        Profile.inst().put(key, ConstantsEx.KEmpty);
        Profile.inst().saveToSp();
        Notifier.inst().notify(NotifyType.profile_change);
    }

    @Override
    public void onUnBindSuccess(@RelatedId int id, IResult r) {
        App.showToast(R.string.account_unbind_succeed);

        List<BindInfo> infoList = Profile.inst().getList(TProfile.bindInfoList);
        boolean flag = true;
        for (BindInfo list : infoList) {
            if (list.getInt(TBindInfo.thirdPartyId) == id) {
                list.clear();
                flag = false;
            }
        }
        if (flag) {
            BindInfo info = new BindInfo();
            info.put(TBindInfo.thirdPartyId, ConstantsEx.KEmpty);
            info.put(TBindInfo.nickName, ConstantsEx.KEmpty);
            infoList.add(info);
        }

        getView().refreshItem(id);
        Profile.inst().put(TProfile.bindInfoList, infoList);
        Profile.inst().saveToSp();
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (!r.isSucceed()) {
            onNetworkError(id, r.getError());
            return;
        }

        switch (id) {
            case BindId.wechat: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(BindId.wechat))) {
                    saveNickName(id, mNickName);
                } else {
                    onUnBindSuccess(id, r);
                }
            }
            break;
            case BindId.sina: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(BindId.sina))) {
                    saveNickName(id, mNickName);
                } else {
                    onUnBindSuccess(id, r);
                }
            }
            break;
            case BindId.yaya: {
                onUnBindSuccess(id, r);
            }
            break;
            case BindId.phone: {
                onUnBindSuccess(id, r, TProfile.mobile);
            }
            break;
            case BindId.email: {
                onUnBindSuccess(id, r, TProfile.email);
            }
            break;
        }
    }
}
