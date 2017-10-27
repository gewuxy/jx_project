package jx.csp.presenter;

import jx.csp.contact.AccountManageContract;
import jx.csp.contact.AccountManageContract.V;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.bind.AccountManageActivity.RelatedId;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
 * @auther Huoxuyu
 * @since 2017/10/26
 */

public class AccountManagePresenterImpl extends PresenterExImpl<AccountManageContract.V> implements AccountManageContract.P {

    private AccountManageContract.V mView;

    public AccountManagePresenterImpl(V v) {
        super(v);
        mView = v;
    }

    @Override
    public void unBindMobileAndEmail(int id, int type) {
        if (id == RelatedId.bind_jingxin) {
            exeNetworkReq(id, UserAPI.bindAccountStatus().thirdPartyId(type).build());
        }else {
            exeNetworkReq(id, UserAPI.unBind(type).build());
        }
    }

    // TODO: 2017/10/26 未完成, 等王兰完成
    @Override
    public void changeThirdPartyBindStatus(int id, int thirdPartyId, String uniqueId, String nickName, String gender, String avatar, TProfile key, String tips) {
        if (TextUtil.isEmpty(Profile.inst().getString(key))) {
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
        }else {
            //已绑定, 解绑请求
            mView.confirmUnBindDialog(tips, v -> {
                if (Util.noNetwork()) {
                    return;
                }
                exeNetworkReq(id, UserAPI.bindAccountStatus().thirdPartyId(thirdPartyId).build());
            });
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        // 绑定的
        Result r = (Result) result;
        switch (id) {
            case RelatedId.bind_wx: {
                mView.unBindSuccess(r, id, TProfile.wx);
            }
            break;
            case RelatedId.bind_sina: {
                mView.unBindSuccess(r, id, TProfile.sina);
            }
            break;
            case RelatedId.bind_facebook: {
                mView.unBindSuccess(r, id, TProfile.facebook);
            }
            break;
            case RelatedId.bind_twitter: {
                mView.unBindSuccess(r, id, TProfile.twitter);
            }
            break;
            case RelatedId.bind_jingxin: {
                mView.unBindSuccess(r, id, TProfile.twitter);
            }
            break;
            case RelatedId.bind_phone: {
                mView.unBindSuccess(r, id, TProfile.mobile);
            }
            break;
            case RelatedId.bind_email: {
                mView.unBindSuccess(r, id, TProfile.email);
            }
            break;
        }
    }
}
